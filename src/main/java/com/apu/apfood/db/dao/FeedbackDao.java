/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apu.apfood.db.dao;

import com.apu.apfood.db.enums.Rating;
import com.apu.apfood.db.models.Feedback;
import com.apu.apfood.helpers.FileHelper;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Maxwell
 */
public class FeedbackDao extends APFoodDao<Feedback> {
    private static final String FEEDBACK_FILEPATH = "/src/main/java/com/apu/apfood/db/datafiles/vendors/";   
    private static final String HEADERS = "feedbackId| feedback| rating| orderId|\n";
    private FileHelper fileHelper = new FileHelper();

    public FeedbackDao()
    {
        super(FEEDBACK_FILEPATH, HEADERS);
    }
    
    @Override
    protected String serialize(Feedback feedback) {
        return feedback.getId() + "| " + 
               feedback.getFeedback()+ "| " + 
               feedback.getRating() + "| " +
               feedback.getOrderId() + "\n";
    }
    
    @Override
    protected Feedback deserialize(String[] data) {
        String feedback = data[1].trim();
        Rating rating = Rating.valueOf(data[2].trim().toUpperCase());
        int orderId = Integer.parseInt(data[3].trim());
        
        return new Feedback(feedback, rating, orderId);
    }
    
    @Override
    public void update(Feedback feedback)
    {
        
    }
    
    public Feedback getFeedbackFromOrderId(int orderId, String vendorName)
    {
        List<Feedback> feedbacks = getAllFeedback(vendorName);
        for (Feedback feedback : feedbacks)
        {
            if (feedback.getOrderId() == orderId)
            {
                return feedback;
            }
        }
        //If there is no matching orderId
        return null;
        
    }
    
    public List<Feedback> getAllFeedback(String vendorName)
    {
        this.filePath = getFullPath(FEEDBACK_FILEPATH + vendorName + "/Reviews.txt");
        List<String[]> rawData = super.getAll();
        List<Feedback> feedbacks = rawData.stream()
                                  .map(this::deserialize)
                                  .collect(Collectors.toList());
        
        return feedbacks;
    }
    
}
