package com.buzzware.iride.response.distanceMatrix;

import java.util.List;

public class DistanceMatrixResponse {
    public List<String> destination_addresses;
    public List<String> origin_addresses;
    public List<Row> rows;
    public String status;
}
