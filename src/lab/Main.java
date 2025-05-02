package lab;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Graph graph = new Graph();
        Scanner scanner = new Scanner(System.in);

        System.out.println("请输入文本文件路径：");
        String filePath = scanner.nextLine();
        graph.buildGraphFromFile(filePath);

        while (true) {
            System.out.println("\n请选择功能：");
            System.out.println("1. 显示有向图");
            System.out.println("2. 查询桥接词");
            System.out.println("3. 生成新文本");
            System.out.println("4. 计算最短路径");
            System.out.println("5. 计算PageRank");
            System.out.println("6. 随机游走");
            System.out.println("7. 导出图像为PNG");
            System.out.println("0. 退出");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("输入无效，请输入数字。");
                continue;
            }

            switch (choice) {
                case 1 -> graph.showDirectedGraph();
                case 2 -> {
                    System.out.print("输入 word1: ");
                    String word1 = scanner.nextLine();
                    System.out.print("输入 word2: ");
                    String word2 = scanner.nextLine();
                    System.out.println(graph.queryBridgeWords(word1, word2));
                }
                case 3 -> {
                    System.out.print("输入新文本：");
                    String newText = scanner.nextLine();
                    System.out.println(graph.generateNewText(newText));
                }
                case 4 -> {
                    System.out.print("输入起始单词：");
                    String word1 = scanner.nextLine();
                    System.out.print("输入目标单词：");
                    String word2 = scanner.nextLine();
                    System.out.println(graph.calcShortestPath(word1, word2));
                }
                case 5 -> {
                    System.out.print("输入单词：");
                    String word = scanner.nextLine();
                    System.out.println("PageRank = " + graph.calPageRank(word));
                }
                case 6 -> System.out.println(graph.randomWalk());
                case 7 -> {
                    System.out.print("请输入图像输出路径（如 output.png）：");
                    String outputPath = scanner.nextLine();
                    graph.exportDirectedGraphImage(outputPath);
                }
                case 0 -> {
                    System.out.println("程序退出");
                    return;
                }
                default -> System.out.println("输入错误，请重新选择！");
            }
        }
    }
}