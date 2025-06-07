package lab;

import java.util.*;

public class GraphPathFinder {
    private Map<String, Map<String, Integer>> adjList = new HashMap<>();

    // 简单将文本拆词，并将相邻单词连成图边，边权为1
    public void loadFromText(String text) {
        String[] words = text.toLowerCase().replaceAll("[^a-z ]", "").split("\\s+");
        for (int i = 0; i < words.length - 1; i++) {
            adjList.putIfAbsent(words[i], new HashMap<>());
            adjList.putIfAbsent(words[i + 1], new HashMap<>());
            adjList.get(words[i]).put(words[i + 1], 1);
            adjList.get(words[i + 1]).put(words[i], 1); // 双向边
        }
    }

    public String calcShortestPath(String word1, String word2) {
        word1 = word1.toLowerCase();
        word2 = word2.toLowerCase();

        if (!adjList.containsKey(word1)) {
            return "No \"" + word1 + "\" in the graph!";
        }
        if (!adjList.containsKey(word2)) {
            return "No \"" + word2 + "\" in the graph!";
        }

        Map<String, Integer> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        Set<String> visited = new HashSet<>();
        PriorityQueue<String> queue = new PriorityQueue<>(Comparator.comparingInt(dist::get));

        for (String node : adjList.keySet()) {
            dist.put(node, Integer.MAX_VALUE);
        }
        dist.put(word1, 0);
        queue.add(word1);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (visited.contains(current)) continue;
            visited.add(current);

            Map<String, Integer> neighbors = adjList.getOrDefault(current, new HashMap<>());
            for (String neighbor : neighbors.keySet()) {
                int weight = neighbors.get(neighbor);
                int newDist = dist.get(current) + weight;
                if (newDist < dist.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    dist.put(neighbor, newDist);
                    prev.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        if (!dist.containsKey(word2) || dist.get(word2) == Integer.MAX_VALUE) {
            return "No path from \"" + word1 + "\" to \"" + word2 + "\"!";
        }

        List<String> path = new LinkedList<>();
        for (String at = word2; at != null; at = prev.get(at)) {
            path.add(0, at);
        }

        return "Shortest path from \"" + word1 + "\" to \"" + word2 + "\":\n" +
                String.join(" -> ", path) + "\nPath weight = " + dist.get(word2);
    }
}
