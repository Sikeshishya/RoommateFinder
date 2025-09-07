package com.roommatefinder.RoommateFinder.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "matching.algorithm")
public class MatchingConfiguration {

    private Map<String, Double> weights;
    private Map<String, Double> thresholds;
    private CacheConfig cache;
    private MLConfig ml;

    // Getters and setters
    public Map<String, Double> getWeights() { return weights; }
    public void setWeights(Map<String, Double> weights) { this.weights = weights; }

    public Map<String, Double> getThresholds() { return thresholds; }
    public void setThresholds(Map<String, Double> thresholds) { this.thresholds = thresholds; }

    public CacheConfig getCache() { return cache; }
    public void setCache(CacheConfig cache) { this.cache = cache; }

    public MLConfig getMl() { return ml; }
    public void setMl(MLConfig ml) { this.ml = ml; }

    public static class CacheConfig {
        private boolean enabled;
        private int ttlHours;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        public int getTtlHours() { return ttlHours; }
        public void setTtlHours(int ttlHours) { this.ttlHours = ttlHours; }
    }

    public static class MLConfig {
        private String modelPath;
        private int retrainIntervalDays;
        private double featureImportanceThreshold;

        public String getModelPath() { return modelPath; }
        public void setModelPath(String modelPath) { this.modelPath = modelPath; }

        public int getRetrainIntervalDays() { return retrainIntervalDays; }
        public void setRetrainIntervalDays(int retrainIntervalDays) { this.retrainIntervalDays = retrainIntervalDays; }

        public double getFeatureImportanceThreshold() { return featureImportanceThreshold; }
        public void setFeatureImportanceThreshold(double featureImportanceThreshold) {
            this.featureImportanceThreshold = featureImportanceThreshold;
        }
    }
}