package de.yogularm.geometry;

public interface NumericFunction {
	float getY(float x);
	float getMinY(float minX, float maxX);
	float getMaxY(float minX, float maxX);
}
