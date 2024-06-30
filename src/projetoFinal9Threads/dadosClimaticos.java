package projetoFinal9Threads;

public class dadosClimaticos {
	 private double average;
	    private double min;
	    private double max;

	    public dadosClimaticos(double average, double min, double max) {
	        this.average = average;
	        this.min = min;
	        this.max = max;
	    }

	    public double getAverage() {
	        return average;
	    }

	    public double getMin() {
	        return min;
	    }

	    public double getMax() {
	        return max;
	    }
	}

