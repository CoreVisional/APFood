package com.apu.apfood.services;

import com.apu.apfood.db.dao.ReviewDao;
import com.apu.apfood.db.dao.UserDao;
import com.apu.apfood.db.models.Review;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Alex
 */
public class ReviewService {
    
    private final ReviewDao reviewDao;
    private final UserDao userDao;
    private final UserService userService;
    
    public ReviewService(ReviewDao reviewDao, UserDao userDao, UserService userService) {
        this.reviewDao = reviewDao;
        this.userDao = userDao;
        this.userService = userService;
    }
    
    public void addReview(Review review, String vendorName) {
        reviewDao.updateFilePath(vendorName);
        reviewDao.add(review);
    }
    
    public List<Review> getCustomerReviewsForVendor(String vendorName) {
        reviewDao.updateFilePath(vendorName);
        List<Review> allReviews = reviewDao.getAllReviews();

        return allReviews.stream()
            .filter(review -> {
                String customerId = userDao.getUserId(String.valueOf(review.getOrderId()), vendorName);
                return userService.isCustomer(Integer.parseInt(customerId));
            })
            .collect(Collectors.toList());
    }
    
    public boolean hasReviewForOrder(int orderId, String vendorName) {
        reviewDao.updateFilePath(vendorName);
        List<Review> allReviews = reviewDao.getAllReviews();
        return allReviews.stream().anyMatch(review -> review.getOrderId() == orderId);
    }
}