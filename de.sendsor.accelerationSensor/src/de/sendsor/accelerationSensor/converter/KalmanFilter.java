/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.sendsor.accelerationSensor.converter;

/**
 * Implementation of an 1D Kalman Filter
 * @author Alexander Stautner
 */
public class KalmanFilter {

//      double measuredValue = 0.0;                     // z_k : measured value with error v_k
//      double prioriValue = 0.0;                       // x-_k : estimate using previous state, action mode, and error
//      double posterioriValue = 0.0;                   // x_k
        private double processNoiseCovariance = 0.0;    // Q : process noise covariance
        private double sensorNoiseCovariance = 0.0;     // R : sensor noise covariance

        private double prioriErrorCovariance;           // P-_k : from update error                                                                 // P-_k : from update error
        private double posterioriErrorCovariance;       // P_k                                                                // P_k

        private double expectedValue = 0.0;

        /**
         * Constructor when priori error covariance equal posteriori error covariance
         * @param errorCovariance
         */
        public KalmanFilter(double errorCovariance) {
                this( errorCovariance, errorCovariance );
        }
        /**
         * Constructor when error covariance unequal posteriori error covariance
         * @param prioriErrorCovariance
         * @param posterioriErrorCovariance
         */
        public KalmanFilter(double prioriErrorCovariance, double posterioriErrorCovariance) {
                this.prioriErrorCovariance = prioriErrorCovariance;
                this.posterioriErrorCovariance = posterioriErrorCovariance;
        }

        /**
         * Performing time update step, when using default process noise covariance
         * @param dt
         */
        public void timeUpdate(double dt) {
                timeUpdate(dt, processNoiseCovariance);
        }

        /**
         * Performing time update step for specific process noise covariance
         * @param dt
         * @param processNoise
         */
        public void timeUpdate(double dt, double processNoise) {
                prioriErrorCovariance = posterioriErrorCovariance + dt*processNoise;
        }

        /**
         * Performing measurement update for default sensor noise covariance returning the value
         * @param prioriValue
         * @param measuredValue
         * @return
         */
        public double measurementUpdate(double prioriValue, double measuredValue) {
                return measurementUpdate(prioriValue, measuredValue, sensorNoiseCovariance);
        }
        /**
         * Performing measurement update for specific sensor noise covariance returning the value
         * @param prioriValue
         * @param measuredValue
         * @param sensorError
         * @return
         */
        public double measurementUpdate(double prioriValue, double measuredValue, double sensorError) {
                double K = prioriErrorCovariance / (prioriErrorCovariance + sensorError);
                double posterioriValue = prioriValue + K*(measuredValue - prioriValue);
                posterioriErrorCovariance = (1-K)*prioriErrorCovariance;
                return posterioriValue;
        }

        /**
         * Performing measurement update for specific sensor noise covariance
         * @param observedValue
         * @param sensorError
         */
        public void measurementUpdate2(double observedValue, double sensorError) {
                this.expectedValue = this.measurementUpdate(this.expectedValue, observedValue, sensorError);
        }

        /**
         * Setting process noise covariance
         * @param q Process noise covariance
         */
        public void setProcessNoiseCovariance(double q) {
                this.processNoiseCovariance = q;
        }

        /**
         * Setting sensor noise covariance
         * @param r sensor noise covariance
         */
        public void setSensorNoiseCovariance(double r) {
                this.sensorNoiseCovariance = r;
        }

        /**
         * Getting process noise covariance
         * @return process noise covariance
         */
        public double getProcessNoiseCovariance() {
                return this.processNoiseCovariance;
        }
        /**
         * Getting sensor noise covariance
         * @return sensor noise covarinace
         */
        public double getSensorNoiseCovariance() {
                return this.sensorNoiseCovariance;
        }
        /**
         * Getting posteriori error covariance
         * @return posteriori error covariance
         */
        public double getPosterioriErrorCovariance() {
                return posterioriErrorCovariance;
        }
        /**
         * Getting posteriori error variance
         * @return posteriori error variance
         */
        public double getPosterioriErrorVariance() {
                return Math.sqrt(posterioriErrorCovariance);
        }

        public Object clone() {
                try {
                        return super.clone();
                } catch (CloneNotSupportedException e) {
                        throw new InternalError(getClass().getName() + ".clone() failed unexpectedly.");
                }
        }
        /**
         * Setting priori error covariance
         * @param d priori error covariance
         */
        public void setPrioriErrorCovariance(double d) {
                prioriErrorCovariance = d;
        }
        /**
         * Setting expected value
         * @param value expected value
         */
        public void setExpectedValue(double value) {
                this.expectedValue = value;
        }
        /**
         * Setting expected value confidence
         * @param error expected value confidence
         */
        public void setExpectedValueConfidence(double error) {
                this.prioriErrorCovariance = error;
                this.posterioriErrorCovariance = error;
        }
        /**
         * Getting expected value
         * @return expected value
         */
        public double getExpectedValue() {
                return this.expectedValue;
        }

}
