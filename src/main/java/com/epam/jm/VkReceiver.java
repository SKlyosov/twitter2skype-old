package com.epam.jm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class VkReceiver implements IPublisher {

	private final static Logger logger = LogManager.getLogger(VkReceiver.class);

	private static final String _URL = "https://api.vk.com/method/wall.get?v=5.28&domain=Depersonilized&filter=owner&extended=1"; 
	
	private JSONArray jsonArray;
	private int curPos;
	private Long curId;
    private IIdsRepository idsRepository;
	
    public VkReceiver(IIdsRepository idsRepository) throws IOException, ParseException {
    	this.idsRepository = idsRepository;
    	init();
    	
    }

	@Override
	public void init() throws ParseException, IOException {
        String line = "";
        URL url = new URL(_URL);
        try (InputStream inputStream = url.openStream();
        		InputStreamReader isReader = new InputStreamReader(inputStream);
        		BufferedReader reader = new BufferedReader(isReader)) {
        	
            line = reader.readLine();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
        if (line != null && !line.isEmpty()) {
            JSONObject json;
			try {
				json = (JSONObject) new JSONParser().parse(line);
	            json = (JSONObject) new JSONParser().parse(json.get("response").toString());
	            jsonArray = (JSONArray) new JSONParser().parse(json.get("items").toString());
			} catch (ParseException e) {
				logger.error(e.getMessage(), e);
				throw e;
			}
        } else {
        	jsonArray = null;
        }
        curPos = 0;
	}
    
	public String receive() throws Exception {
		String result = null; 
		if (jsonArray != null) {
			while (curPos < jsonArray.size()) {
				String item = jsonArray.get(curPos).toString();
		        JSONObject json = null;
				try {
					json = (JSONObject) new JSONParser().parse(item);
			        curId = Long.parseLong(json.get("id").toString());
				} catch (ParseException e) {
					logger.error(e.getMessage(), e);
					throw e;
				}
		        if (!idsRepository.contains(curId)) {
		        	result = getTextFromJson(json);
		        	idsRepository.save(curId);
		        	curPos++;
		        } else  {
		        	curPos++;
		        }
			}
		}
 		logger.info("Gettting a Vk message \"{}\"", result);
		return result;
	}

	private String getTextFromJson(JSONObject json) throws ParseException {
		String text;
		
        text = json.get("text").toString();
        if ("".equals(text)) {
            if (json.get("copy_history") != null) {
                JSONArray json3;
				try {
					json3 = (JSONArray) new JSONParser().parse(json.get("copy_history").toString());
	                if (json3.size() > 0) {
	                    JSONObject json4 = (JSONObject) new JSONParser().parse(json3.get(0).toString());
	                    text = json4.get("text").toString();
	                }
				} catch (ParseException e) {
					logger.error(e.getMessage(), e);
					throw e;
				}
            }
        }
        Date date = new Date(Long.parseLong(json.get("date").toString()) * 1000);
        
		String attachment = getAttachment(json);
        String sText = new StringBuilder("@Depersonilized (VK) от ")
        	.append(DateHelper.getShiftedDateString(date, 7))
        	.append(" ( http://vk.com/Depersonilized?w=wall-0_")
        	.append(curId)
        	.append(" ): \r\n")
        	.append(text)
        	.append(attachment != null && !attachment.isEmpty() ? attachment : "")
        	.toString();
		
		return sText;
		
	}
	
    private String getAttachment(JSONObject js) throws ParseException {
        String att = "";
        if (js.get("attachments") != null) {
            JSONArray attach;
			try {
				attach = (JSONArray) new JSONParser().parse(js.get("attachments").toString());
	            if (attach.size() > 0) {
	                JSONObject attach1 = (JSONObject) new JSONParser().parse(attach.get(0).toString());
	                if (attach1.get("type") != null && "photo".equals(attach1.get("type").toString())) {
	                    JSONObject photo = (JSONObject) new JSONParser().parse(attach1.get("photo").toString());
	                    if (photo.get("photo_604") != null) {
	                        att = photo.get("photo_604").toString();
	                    }
	                }
	            }
			} catch (ParseException e) {
				logger.error(e.getMessage(), e);
				throw e;
			}
        }
        return att;
    }


}
