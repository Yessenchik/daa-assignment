package org.example.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.model.*;
import java.io.*;
import java.util.*;

public class JSONHandler {
    private final ObjectMapper mapper;

    public JSONHandler() {
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public InputData readInput(String filename) throws IOException {
        return mapper.readValue(new File(filename), InputData.class);
    }

    public void writeInput(String filename, InputData input) throws IOException {
        mapper.writeValue(new File(filename), input);
    }

    public void writeOutput(String filename, OutputData output) throws IOException {
        mapper.writeValue(new File(filename), output);
    }

    public Graph createGraph(InputData.GraphData graphData) {
        List<Edge> edges = new ArrayList<>();
        for (InputData.EdgeData edgeData : graphData.getEdges()) {
            edges.add(new Edge(edgeData.getFrom(), edgeData.getTo(), edgeData.getWeight()));
        }
        return new Graph(graphData.getNodes(), edges);
    }
}