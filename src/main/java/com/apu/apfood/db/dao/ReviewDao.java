package com.apu.apfood.db.dao;

import com.apu.apfood.db.models.Review;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Alex
 */
public class ReviewDao extends APFoodDao<Review> {
    
    private static final String REVIEWS_FILEPATH = "/src/main/java/com/apu/apfood/db/datafiles/vendors/";   
    private static final String HEADERS = "id| orderId| feedback| rating\n";
    
    public ReviewDao() {
        
    }
    
    public ReviewDao(String vendorName) {
        super(REVIEWS_FILEPATH, HEADERS);
    }
    
    public void updateFilePath(String vendorName) {
        this.filePath = getFullPath(REVIEWS_FILEPATH + vendorName + "/Reviews.txt");
    }
    
    public List<Review> getAllReviews() {

        List<String[]> rawData = super.getAll();
        
        return rawData.stream()
                       .map(this::deserialize)
                       .collect(Collectors.toList());
    }
    
    @Override
    protected String serialize(Review review) {
        return review.getOrderId() + "| " +
               review.getFeedback() + "| " +
               review.getRating() + "\n";
    }
    
    @Override
    protected Review deserialize(String[] data) {
        int id = Integer.parseInt(data[0].trim());
        int orderId = Integer.parseInt(data[1].trim());
        String feedback = data[2].trim();
        int rating = Integer.parseInt(data[3].trim());
        
        return new Review(id, orderId, feedback, rating);
    }

    @Override
    public void update(Review review) {

    }
}