package ru.artosoft.vcsvpl.service;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DrawProgramService {
    static String lastNode = "";
    public static StringBuilder drawFullCode(String fileName) {
        StringBuilder jsCode = new StringBuilder();
        jsCode.append("var canvas = document.getElementById('myCanvas');\n");
        jsCode.append("var ctx = canvas.getContext('2d');\n");
        jsCode.append("ctx.font = '12px Arial';\n");
        jsCode.append("ctx.textAlign = 'center';\n");
        jsCode.append("ctx.textBaseline = 'middle';\n");
        jsCode.append("\n");
        int x = 200; // Initial x position
        int y = 50; // Initial y position
        try {
            // Load and parse the XML file
            File xmlFile = new File(fileName);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            // Normalize the document
            doc.getDocumentElement().normalize();

            // Get the root element
            Node root = doc.getDocumentElement();

            // Find all "function" tags and process their child nodes
            NodeList functionNodes = root.getOwnerDocument().getElementsByTagName("function");
            StringBuilder newCode = new StringBuilder();
            for (int i = 0; i < functionNodes.getLength(); i++) {
                Node function = functionNodes.item(i);
                jsCode.append(processNode(function, x, y));
                x += 200;
            }
            //System.out.println(jsCode);
        } catch (Exception e) {
            e.printStackTrace();
            jsCode.append("ctx.fillText('" + "Ошибка создания графика" + "', " + (x + 50) + ", " + (y + 25) + ");\n");
        }

        return jsCode;
    }

    public static StringBuilder processNode(Node node, int x, int y) {
        lastNode = "";
        StringBuilder code = new StringBuilder();
        //System.out.println(node.getNodeName());
        Element nodeElement = (Element) node;
        if (lastNode.equals("if")) {
            y += 100;
        }
        //отрисовка блоков
        if (node.getNodeName().equals("function")) {

            code.append("ctx.fillText('" + nodeElement.getAttribute("name") + "', " + (x - 10) + ", " + y + ");\n");
            code.append("\n");
        } else if (node.getNodeName().equals("body")) {
            code.append("ctx.beginPath();\n");
            code.append("ctx.ellipse(" + (x + 50) + ", " + (y + 25) + ", 50, 25, 0, 0, Math.PI * 2);\n");
            code.append("ctx.stroke();\n");
            code.append("\n");

            code.append("ctx.fillText('" + "Главная" + "', " + (x + 50) + ", " + (y + 25) + ");\n");
            code.append("\n");
        } else if (node.getNodeName().equals("declare")) {
            code.append("ctx.beginPath();\n");
            code.append("ctx.rect(" + x + ", " + y + ", 100, 50);\n");
            code.append("ctx.stroke();\n");
            code.append("\n");

            code.append("ctx.fillText('" + nodeElement.getAttribute("type") + " "
                    + (nodeElement.getAttribute("array").equals("True") ? "array " : "") +
                    nodeElement.getAttribute("name") +
                    (nodeElement.getAttribute("array").equals("True") ? "[" + nodeElement.getAttribute("size") + "]" : "")
                    + "', " + (x + 50) + ", " + (y + 25) + ");\n");
            code.append("\n");

            code.append("ctx.beginPath();\n");
            code.append("ctx.moveTo(" + (x + 10) + ", " + y + ");");
            code.append("ctx.lineTo(" + (x + 10) + ", " + (y + 50) + ");");
            code.append("ctx.stroke();");
            code.append("\n");

            code.append("ctx.beginPath();\n");
            code.append("ctx.moveTo(" + x + ", " + (y + 10) + ");");
            code.append("ctx.lineTo(" + (x + 100) + ", " + (y + 10) + ");");
            code.append("ctx.stroke();");
            code.append("\n");

        } else if (node.getNodeName().equals("assign")) {
            code.append("ctx.beginPath();\n");
            code.append("ctx.rect(" + x + ", " + y + ", 100, 50);\n");
            code.append("ctx.stroke();\n");
            code.append("\n");

            code.append("ctx.fillText('" + nodeElement.getAttribute("variable") + "="
                    + nodeElement.getAttribute("expression")
                    + "', " + (x + 50) + ", " + (y + 25) + ");\n");
            code.append("\n");
        } else if (node.getNodeName().equals("output")) {
            code.append("ctx.beginPath();\n");
            code.append("ctx.moveTo(" + (x + 20) + ", " + y + ");\n");
            code.append("ctx.lineTo(" + (x + 100) + "," + y + ");\n");
            code.append("ctx.lineTo(" + (x + 80) + "," + (y + 50) + ");\n");
            code.append("ctx.lineTo(" + x + "," + (y + 50) + ");\n");
            code.append("ctx.closePath()\n");
            code.append("ctx.stroke();\n");
            code.append("\n");

            code.append("ctx.fillText('Вывод "
                    + nodeElement.getAttribute("expression")
                    + (nodeElement.getAttribute("newline").equals("False") ? "..." : "")
                    + "', " + (x + 50) + ", " + (y + 25) + ");\n");
            code.append("\n");
        } else if (node.getNodeName().equals("input")) {
            code.append("ctx.beginPath();\n");
            code.append("ctx.moveTo(" + (x + 20) + ", " + y + ");\n");
            code.append("ctx.lineTo(" + (x + 100) + "," + y + ");\n");
            code.append("ctx.lineTo(" + (x + 80) + "," + (y + 50) + ");\n");
            code.append("ctx.lineTo(" + x + "," + (y + 50) + ");\n");
            code.append("ctx.closePath()\n");
            code.append("ctx.stroke();\n");
            code.append("\n");

            code.append("ctx.fillText('Ввод "
                    + nodeElement.getAttribute("variable")
                    + "', " + (x + 50) + ", " + (y + 25) + ");\n");
            code.append("\n");
        } else if (node.getNodeName().equals("call")) {
            code.append("ctx.beginPath();\n");
            code.append("ctx.rect(" + x + ", " + y + ", 100, 50);\n");
            code.append("ctx.stroke();\n");
            code.append("\n");

            code.append("ctx.fillText('" + nodeElement.getAttribute("expression")
                    + "', " + (x + 50) + ", " + (y + 25) + ");\n");
            code.append("\n");

            code.append("ctx.beginPath();\n");
            code.append("ctx.moveTo(" + (x + 10) + ", " + y + ");");
            code.append("ctx.lineTo(" + (x + 10) + ", " + (y + 50) + ");");
            code.append("ctx.stroke();");
            code.append("\n");

            code.append("ctx.beginPath();\n");
            code.append("ctx.moveTo(" + (x + 90) + ", " + y + ");");
            code.append("ctx.lineTo(" + (x + 90) + ", " + (y + 50) + ");");
            code.append("ctx.stroke();");
            code.append("\n");
        } else if (node.getNodeName().equals("if")) {
            code.append("ctx.beginPath();\n");
            code.append("ctx.moveTo(" + (x + 50) + ", " + y + ");\n");
            code.append("ctx.lineTo(" + (x + 100) + "," + (y + 25) + ");\n");
            code.append("ctx.lineTo(" + (x + 50) + "," + (y + 50) + ");\n");
            code.append("ctx.lineTo(" + x + "," + (y + 25) + ");\n");
            code.append("ctx.closePath()\n");
            code.append("ctx.stroke();\n");
            code.append("\n");

            code.append("ctx.fillText('"
                    + nodeElement.getAttribute("expression")
                    + "', " + (x + 50) + ", " + (y + 25) + ");\n");
            code.append("\n");
        }

        //отрисовка стрелочек
        if (node.getNodeName().equals("then")) {
            code.append("ctx.beginPath();\n");
            code.append("ctx.moveTo(" + (x + 100) + ", " + (y + 25) + ");\n");
            code.append("ctx.lineTo(" + (x + 150) + ", " + (y + 25) + ");\n");
            code.append("ctx.lineTo(" + (x + 150) + ", " + (y + 75) + ");\n");
            code.append("ctx.stroke();\n");
            code.append("ctx.fillText('Да', " + (x + 110) + "," +  (y + 15) + ");\n");
            x += 100;
            y += 75;
            code.append("\n");
        } else if (node.getNodeName().equals("else")) {
            System.out.println(x + " " + y);
            code.append("ctx.beginPath();\n");
            code.append("ctx.moveTo(" + x + ", " + (y - 50) + ");\n");
            code.append("ctx.lineTo(" + (x - 50) + ", " + (y - 50) + ");\n");
            code.append("ctx.lineTo(" + (x - 50) + ", " + y + ");\n");
            code.append("ctx.stroke();\n");
            code.append("ctx.fillText('нет', " + (x - 10) + "," +  (y - 60) + ");\n");
            x -= 100;
            code.append("\n");
        }
        else if (!node.getNodeName().equals("function") && !node.getNodeName().equals("parameters") && !node.getNodeName().equals("if")) {
            code.append("ctx.beginPath();\n");
            code.append("ctx.moveTo(" + (x + 50) + ", " + (y + 50) + ");\n");
            y += 75;
            code.append("ctx.lineTo(" + (x + 50) + ", " + y + ");\n");
            code.append("ctx.stroke();\n");
            code.append("\n");
        }




        // Process child nodes recursively
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE && !child.getNodeName().equals("parameters")) {
                code.append(processNode(child, x, y));
                y += 75;
            }
        }

        if (node.getNodeName().equals("then")) {
            code.append("ctx.beginPath();\n");
            code.append("ctx.moveTo(" + (x + 50) + ", " + y + ");\n");
            code.append("ctx.lineTo(" + (x - 50) + ", " + y + ");\n");
            code.append("ctx.stroke();\n");
            code.append("\n");
        }
        else if (node.getNodeName().equals("else")) {
            code.append("ctx.beginPath();\n");
            code.append("ctx.moveTo(" + (x + 50) + ", " + y + ");\n");
            code.append("ctx.lineTo(" + (x + 150) + ", " + y + ");\n");
            code.append("ctx.stroke();\n");
            code.append("\n");
            code.append("ctx.beginPath();\n");
            code.append("ctx.moveTo(" + (x + 150) + ", " + y + ");\n");
            code.append("ctx.lineTo(" + (x + 150) + ", " + (y+25) + ");\n");
            code.append("ctx.stroke();\n");
            code.append("\n");
        }
        else if (node.getNodeName().equals("if")) {
            lastNode = "if";
        }
        else if (node.getNodeName().equals("body")) {
            if (lastNode.equals("if"))  y += 100;
            code.append("ctx.beginPath();\n");
            code.append("ctx.ellipse(" + (x + 50) + ", " + (y + 25) + ", 50, 25, 0, 0, Math.PI * 2);\n");
            code.append("ctx.stroke();\n");
            code.append("\n");

            code.append("ctx.fillText('" + "Конец" + "', " + (x + 50) + ", " + (y + 25) + ");\n");
        }

        return code;
    }
}
