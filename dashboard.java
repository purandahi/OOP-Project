package com.pgno248.model;

import java.io.*;
import java.util.*;

public class dashboard {
    private static final String DELIVERY_FILE = "C:\\Users\\pulilna\\Desktop\\PNGO248\\src\\main\\resources\\deliverDetails.txt";
    private static final String CHECKOUT_FILE = "C:\\Users\\pulilna\\Desktop\\PNGO248\\src\\main\\resources\\Checkout.txt";
    private static final String ORDER_FILE = "C:\\Users\\pulilna\\Desktop\\PNGO248\\src\\main\\resources\\oder.txt";

    public static Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        try {
            // Get all orders
            List<String[]> orders = readCheckoutDetails();
            List<String[]> deliveries = readDeliveryDetails();
            Map<String, String> orderStatuses = readOrderStatuses();

            // Calculate statistics
            int totalOrders = orders.size();
            int processingOrders = 0;
            int completedOrders = 0;
            double totalEarnings = 0.0;
            Set<String> uniqueCustomers = new HashSet<>();

            // Process orders
            for (String[] order : orders) {
                if (order.length >= 3) {
                    // Calculate total earnings
                    try {
                        totalEarnings += Double.parseDouble(order[2]);
                    } catch (NumberFormatException e) {
                        // Skip invalid total
                    }

                    // Count order statuses
                    String orderId = order[0];
                    String status = orderStatuses.getOrDefault(orderId, "Processing");
                    if ("Complete".equals(status)) {
                        completedOrders++;
                    } else {
                        processingOrders++;
                    }
                }
            }

            // Count unique customers
            for (String[] delivery : deliveries) {
                if (delivery.length >= 2 && !delivery[1].trim().isEmpty()) {
                    uniqueCustomers.add(delivery[1]);
                }
            }

            // Store statistics
            stats.put("totalOrders", totalOrders);
            stats.put("processingOrders", processingOrders);
            stats.put("completedOrders", completedOrders);
            stats.put("totalEarnings", String.format("%.2f", totalEarnings));
            stats.put("uniqueCustomers", uniqueCustomers.size());

        } catch (IOException e) {
            System.err.println("Error reading files: " + e.getMessage());
        }
        return stats;
    }

    private static List<String[]> readCheckoutDetails() throws IOException {
        List<String[]> details = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(CHECKOUT_FILE))) {
            String line;
            while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                details.add(line.split(":"));
            }
        }
        return details;
    }

    private static List<String[]> readDeliveryDetails() throws IOException {
        List<String[]> details = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DELIVERY_FILE))) {
            String line;
            while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                details.add(line.split(":"));
            }
        }
        return details;
    }

    private static Map<String, String> readOrderStatuses() {
        Map<String, String> statuses = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ORDER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length >= 2) {
                    statuses.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading order statuses: " + e.getMessage());
        }
        return statuses;
    }
}
