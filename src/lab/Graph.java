package lab;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Graph {
    private Map<String, Map<String, Integer>> adjList = new HashMap<>();
    private Set<String> allNodes = new HashSet<>();
    private List<String> wordSequence = new ArrayList<>();  // 存储原始词序列

    public void buildGraphFromFile(String filePath) {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            wordSequence.clear();
            List<String> words = new ArrayList<>();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().toLowerCase().replaceAll("[^a-zA-Z\\s]", " ");
                words.addAll(Arrays.asList(line.trim().split("\\s+")));
            }

            for (int i = 0; i < words.size() - 1; i++) {
                String from = words.get(i);
                String to = words.get(i + 1);
                if (from.isEmpty() || to.isEmpty()) continue;

                adjList.putIfAbsent(from, new HashMap<>());
                Map<String, Integer> edges = adjList.get(from);
                edges.put(to, edges.getOrDefault(to, 0) + 1);

                allNodes.add(from);
                allNodes.add(to);
            }

            wordSequence.addAll(words);
            System.out.println("图构建完成，节点数：" + allNodes.size());
        } catch (Exception e) {
            System.out.println("读取文件出错: " + e.getMessage());
        }
    }

    public void exportDirectedGraphImage(String outputPath) {
        lab.Graphviz gv = new lab.Graphviz();
        gv.addln(gv.start_graph());
        gv.addln("edge[fontname=\"DFKai-SB\" fontsize=12 fontcolor=\"black\" color=\"blue\" style=\"solid\"]");
        gv.addln("size =\"10,10\";");

        Set<String> allGraphNodes = new HashSet<>();
        allGraphNodes.addAll(adjList.keySet());
        for (Map<String, Integer> edges : adjList.values()) {
            allGraphNodes.addAll(edges.keySet());
        }

        for (String node : allGraphNodes) {
            gv.addln("\"" + node + "\" [shape = ellipse, style = filled, color = lightgrey];");
        }

        for (String from : adjList.keySet()) {
            for (Map.Entry<String, Integer> entry : adjList.get(from).entrySet()) {
                String to = entry.getKey();
                int weight = entry.getValue();
                gv.addln("\"" + from + "\" -> \"" + to + "\" [label=\"" + weight + "\"];");
            }
        }

        gv.addln(gv.end_graph());
        byte[] img = gv.getGraph(gv.getDotSource(), "png");
        int result = gv.writeGraphToFile(img, new File(outputPath));
        if (result == 1) {
            System.out.println("图像已成功导出到：" + outputPath);
        } else {
            System.out.println("图像导出失败！");
        }
    }

    public void showDirectedGraph() {
        System.out.println("图的邻接表展示：");
        for (String from : adjList.keySet()) {
            System.out.print(from + " -> ");
            Map<String, Integer> edges = adjList.get(from);
            List<String> edgeStrings = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : edges.entrySet()) {
                edgeStrings.add(entry.getKey() + "(" + entry.getValue() + ")");
            }
            System.out.println(String.join(", ", edgeStrings));
        }
    }

    public String queryBridgeWords(String word1, String word2) {
        word1 = word1.toLowerCase();
        word2 = word2.toLowerCase();

        Set<String> allWords = new HashSet<>(wordSequence);
        if (!allWords.contains(word1)) {
            if (!allWords.contains(word2)) {
                return "No \"" + word1 + "\" and \"" + word2 + "\" in the graph!";
            }
            return "No \"" + word1 + "\" in the graph!";
        }
        if (!allWords.contains(word2)) {
            return "No \"" + word2 + "\" in the graph!";
        }

        Set<String> bridgeWords = new HashSet<>();
        for (int i = 0; i < wordSequence.size() - 2; i++) {
            String w1 = wordSequence.get(i);
            String mid = wordSequence.get(i + 1);
            String w2 = wordSequence.get(i + 2);
            if (w1.equals(word1) && w2.equals(word2)) {
                bridgeWords.add(mid);
            }
        }

        if (bridgeWords.isEmpty()) {
            return "No bridge words from \"" + word1 + "\" to \"" + word2 + "\"!";
        }

        List<String> bridgeList = new ArrayList<>(bridgeWords);
        if (bridgeList.size() == 1) {
            return "The bridge word from \"" + word1 + "\" to \"" + word2 + "\" is: \"" + bridgeList.get(0) + "\".";
        } else {
            return "The bridge words from \"" + word1 + "\" to \"" + word2 + "\" are: " + String.join(", ", bridgeList) + ".";
        }
    }


    public String generateNewText(String inputText) {
        inputText = inputText.toLowerCase().replaceAll("[^a-zA-Z\\s]", " ");
        String[] words = inputText.trim().split("\\s+");

        if (words.length < 2) return inputText;

        StringBuilder result = new StringBuilder();
        Random rand = new Random();

        for (int i = 0; i < words.length - 1; i++) {
            String word1 = words[i];
            String word2 = words[i + 1];
            result.append(word1);

            Set<String> bridges = new HashSet<>();
            if (adjList.containsKey(word1)) {
                Map<String, Integer> nextFromWord1 = adjList.get(word1);
                for (String mid : nextFromWord1.keySet()) {
                    if (adjList.containsKey(mid) && adjList.get(mid).containsKey(word2)) {
                        bridges.add(mid);
                    }
                }
            }

            if (!bridges.isEmpty()) {
                List<String> bridgeList = new ArrayList<>(bridges);
                String selectedBridge = bridgeList.get(rand.nextInt(bridgeList.size()));
                result.append(" ").append(selectedBridge);
            }

            result.append(" ");
        }

        result.append(words[words.length - 1]);
        return result.toString();
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

        return "Shortest path from \"" + word1 + "\" to \"" + word2 + "\":\n" + String.join(" -> ", path) + "\nPath weight = " + dist.get(word2);
    }

    public Double calPageRank(String word) {
        word = word.toLowerCase();

        // 构建完整节点集合（包括仅作为目标节点的）
        Set<String> allNodeSet = new HashSet<>();
        allNodeSet.addAll(adjList.keySet());
        for (Map<String, Integer> edges : adjList.values()) {
            allNodeSet.addAll(edges.keySet());
        }

        if (!allNodeSet.contains(word)) {
            System.out.println("Word \"" + word + "\" not found in graph.");
            return 0.0;
        }

        final double d = 0.85;
        final int maxIterations = 100;
        final double epsilon = 1e-6;
        int N = allNodeSet.size();

        // 初始化 PageRank 值
        Map<String, Double> pr = new HashMap<>();
        for (String node : allNodeSet) {
            pr.put(node, 1.0 / N);
        }

        // 构建每个节点的入边节点集合
        Map<String, Set<String>> incoming = new HashMap<>();
        for (String node : allNodeSet) {
            incoming.put(node, new HashSet<>());
        }
        for (String from : adjList.keySet()) {
            for (String to : adjList.get(from).keySet()) {
                incoming.get(to).add(from);
            }
        }

        // ✅ 打印指向该节点的节点集合，调试用
        System.out.println("Incoming nodes pointing to \"" + word + "\": " + incoming.get(word));

        // 开始迭代
        for (int iter = 0; iter < maxIterations; iter++) {
            Map<String, Double> newPr = new HashMap<>();

            // ✅ Step 1: 计算所有 dangling nodes（出度为0）的 PageRank 总和
            double danglingSum = 0.0;
            for (String node : allNodeSet) {
                if (!adjList.containsKey(node) || adjList.get(node).isEmpty()) {
                    danglingSum += pr.get(node);
                }
            }

            // Step 2: 更新每个节点的新 PR 值
            for (String node : allNodeSet) {
                double sum = 0.0;

                for (String v : incoming.get(node)) {
                    int outDegree = adjList.get(v).size();
                    if (outDegree > 0) {
                        sum += pr.get(v) / outDegree;
                    }
                }

                double danglingContribution = danglingSum / N;
                newPr.put(node, (1 - d) / N + d * (sum + danglingContribution));
            }

            // Step 3: 检查是否收敛
            double delta = 0.0;
            for (String node : allNodeSet) {
                delta += Math.abs(pr.get(node) - newPr.get(node));
            }

            pr = newPr;
            if (delta < epsilon) break;
        }

        return pr.getOrDefault(word, 0.0);
    }


    public String randomWalk() {
        if (adjList.isEmpty()) return "图为空，无法执行随机游走！";

        StringBuilder walkResult = new StringBuilder();
        Set<String> visitedEdges = new HashSet<>();
        Random rand = new Random();

        List<String> nodes = new ArrayList<>(adjList.keySet());
        String current = nodes.get(rand.nextInt(nodes.size()));
        walkResult.append(current);

        while (true) {
            Map<String, Integer> neighbors = adjList.getOrDefault(current, new HashMap<>());
            if (neighbors.isEmpty()) break;

            List<String> neighborList = new ArrayList<>(neighbors.keySet());
            String next = neighborList.get(rand.nextInt(neighborList.size()));

            String edgeKey = current + "->" + next;
            if (visitedEdges.contains(edgeKey)) {
                walkResult.append(" -> ").append(next);
                break;
            }

            visitedEdges.add(edgeKey);
            walkResult.append(" -> ").append(next);
            current = next;
        }

        try {
            Files.writeString(Path.of("random_walk.txt"), walkResult.toString());
        } catch (IOException e) {
            System.out.println("写入文件失败: " + e.getMessage());
        }

        return "随机游走结果：\n" + walkResult.toString();
    }
}
