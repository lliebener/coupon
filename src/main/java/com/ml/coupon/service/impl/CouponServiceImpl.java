package com.ml.coupon.service.impl;

import com.google.common.collect.Lists;
import com.ml.coupon.service.CouponService;
import com.ml.coupon.service.util.CouponUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponUtil couponUtil;

    @Override
    public List<String> calculate(Map<String, Float> items, Float amount) {
        List<String> bestItem = new ArrayList<>();
        calculate(items,amount, new ArrayList(),bestItem, false);
        return bestItem;
    }

    /**
     * Backtracking scan algorithm to determine the list of items that maximize the amount to spend
     *
     * @param items Favorite Item Map (item code as key and item price as value)
     * @param amount Coupon maximum amount to spent
     * @param currentItems List of currently selected items, in each iteration
     * @param bestItems List of items that maximize the amount to spend
     * @param exceedMaxAmount Flag to indicate if the selected items exceed the maximum amount to spend.
     *                        It is used as a leaf condition of the backtracking algorithm
     * @return List of items that maximize the amount to spend
     */
    private void calculate(Map<String,Float> items, Float amount, List<String> currentItems, List<String> bestItems, boolean exceedMaxAmount) {
        // The maximum amount to spend has been reached
        if (exceedMaxAmount) {
            // If the current items price is greater than the best items price, the current items is the best option
            if (couponUtil.calculateItemsPrice(items, currentItems)>couponUtil.calculateItemsPrice(items,bestItems)) {
                bestItems.clear();
                bestItems.addAll(currentItems);
            }
        } else {
            for (String item: items.keySet()) {
                if (!currentItems.contains(item)) {
                    if (!exceedsAmount(items,currentItems,amount,item)) {
                        currentItems.add(item);
                        calculate(items,amount,currentItems,bestItems,false);
                        currentItems.remove(item);
                    }
                    else {
                        calculate(items,amount,currentItems,bestItems,true);
                    }
                }
            }
        }
    }

    /**
     * Determines if by adding the selected item, the price of it exceeds the maximum amount to spend
     *
     * @param itemsPrice Item Map (item code as key and item price as value)
     * @param items Current selected items
     * @param amount Maximun amount to spent
     * @param itemId Selected Item ID
     * @return True if the selected item price exceeds the maximum amount to spend
     */
    private boolean exceedsAmount(Map<String,Float> itemsPrice, List<String> items, Float amount, String itemId) {
        return couponUtil.calculateItemsPrice(itemsPrice,items)+itemsPrice.get(itemId)>amount;
    }
}
