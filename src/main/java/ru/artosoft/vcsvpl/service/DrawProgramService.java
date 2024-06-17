package ru.artosoft.vcsvpl.service;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;


public class DrawProgramService {
    static String lastNode = "";
    static int childsThen = 0, childsElse = 0, lastX = 0, lastY = 0, lastIfX = 0, lastIfY = 0, childsWhile = 0, childsFor = 0;
    public static StringBuilder drawFullCode(byte[] fileText, String canvasId) {
        StringBuilder jsCode = new StringBuilder();
        jsCode.append("var canvas = document.getElementById('" + canvasId+ "');\n");
        jsCode.append("var ctx = canvas.getContext('2d');\n");
        jsCode.append("ctx.font = '12px Arial';\n");
        jsCode.append("ctx.textAlign = 'center';\n");
        jsCode.append("ctx.textBaseline = 'middle';\n");
        jsCode.append("\n");
        lastX = 200;
        lastY = 50;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputSource xmlFile = new InputSource(new ByteArrayInputStream(fileText));
            Document doc = dBuilder.parse(xmlFile);

            doc.getDocumentElement().normalize();

            Node root = doc.getDocumentElement();

            NodeList functionNodes = root.getOwnerDocument().getElementsByTagName("function");
            StringBuilder newCode = new StringBuilder();
            for (int i = 0; i < functionNodes.getLength(); i++) {
                Node function = functionNodes.item(i);
                jsCode.append(processNode(function, lastX, lastY));
                lastX += 300;
                lastY = 50;
            }
            //System.out.println(jsCode);
        } catch (Exception e) {
            e.printStackTrace();
            jsCode.append("ctx.fillText('" + "Ошибка создания графика" + "', " + (lastX + 50) + ", " + (lastY + 25) + ");\n");
        }

        return jsCode;
    }

    public static StringBuilder processNode(Node node, int x, int y) {
        StringBuilder code = new StringBuilder();
        Element nodeElement = (Element) node;

        if (lastNode.equals("if")) {x = lastX; y = lastY;}
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
            lastNode = "declare";
        } else if (node.getNodeName().equals("assign")) {
            code.append("ctx.beginPath();\n");
            code.append("ctx.rect(" + x + ", " + y + ", 100, 50);\n");
            code.append("ctx.stroke();\n");
            code.append("\n");

            code.append("ctx.fillText('" + nodeElement.getAttribute("variable") + "="
                    + nodeElement.getAttribute("expression")
                    + "', " + (x + 50) + ", " + (y + 25) + ");\n");
            code.append("\n");
            lastNode = "assign";
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
            lastNode = "output";
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
            lastNode = "input";
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
            lastNode = "call";
        }
        else if (node.getNodeName().equals("if")) {
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
            for (int i = 0; i < node.getChildNodes().getLength(); i++) {
                if (node.getChildNodes().item(i).getNodeName().equals("then"))
                    childsThen = node.getChildNodes().item(i).getChildNodes().getLength();
                if (node.getChildNodes().item(i).getNodeName().equals("else"))
                    childsElse = node.getChildNodes().item(i).getChildNodes().getLength();
            }
            lastIfX = x;
            lastIfY = y;
        }
        else if (node.getNodeName().equals("while")) {
            childsWhile = node.getChildNodes().getLength();
            code.append("ctx.beginPath();\n");
            code.append("ctx.moveTo(" + x + ", " + y + ");\n");
            code.append("ctx.lineTo(" + (x + 100) + "," + y + ");\n");
            code.append("ctx.lineTo(" + (x + 115) + "," + (y + 25) + ");\n");
            code.append("ctx.lineTo(" + (x + 100) + "," + (y + 50) + ");\n");
            code.append("ctx.lineTo(" + x + "," + (y + 50) + ");\n");
            code.append("ctx.lineTo(" + (x - 15) + "," + (y + 25) + ");\n");
            code.append("ctx.closePath()\n");
            code.append("ctx.stroke();\n");
            code.append("\n");

            code.append("ctx.fillText('"
                    + nodeElement.getAttribute("expression")
                    + "', " + (x + 50) + ", " + (y + 25) + ");\n");
            code.append("\n");

            code.append("ctx.fillText('Нет', " + (x + 30) + "," + (y + 65) + ");\n");

            if (childsWhile == 0) {
                code.append("ctx.beginPath();\n");
                code.append("ctx.moveTo(" + (x + 115) + ", " + (y + 25) + ");\n");
                code.append("ctx.lineTo(" + (x + 165) + ", " + (y + 25) + ");\n");
                code.append("ctx.lineTo(" + (x + 165) + ", " + (y + 65) + ");\n");
                code.append("ctx.lineTo(" + (x + 80) + ", " + (y + 65) + ");\n");
                code.append("ctx.lineTo(" + (x + 80) + ", " + (y + 50) + ");\n");
                code.append("ctx.stroke();\n");
                code.append("ctx.fillText('Да', " + (x + 140) + "," + (y + 15) + ");\n");
                code.append("\n");
            }
        }
        else if (node.getNodeName().equals("for")) {
            childsFor = node.getChildNodes().getLength();
            code.append("ctx.beginPath();\n");
            code.append("ctx.moveTo(" + x + ", " + y + ");\n");
            code.append("ctx.lineTo(" + (x + 100) + "," + y + ");\n");
            code.append("ctx.lineTo(" + (x + 115) + "," + (y + 25) + ");\n");
            code.append("ctx.lineTo(" + (x + 100) + "," + (y + 50) + ");\n");
            code.append("ctx.lineTo(" + x + "," + (y + 50) + ");\n");
            code.append("ctx.lineTo(" + (x - 15) + "," + (y + 25) + ");\n");
            code.append("ctx.closePath()\n");
            code.append("ctx.stroke();\n");
            code.append("\n");

            code.append("ctx.fillText('"
                    + nodeElement.getAttribute("variable")
                    + " = от " + nodeElement.getAttribute("start") + " до " + nodeElement.getAttribute("end") + " с шагом " + nodeElement.getAttribute("step") + "', " + (x + 50) + ", " + (y + 25) + ");\n");
            code.append("\n");

            code.append("ctx.fillText('Конец цикла', " + x + "," + (y + 65) + ");\n");

            if (childsFor == 0) {
                code.append("ctx.beginPath();\n");
                code.append("ctx.moveTo(" + (x + 115) + ", " + (y + 25) + ");\n");
                code.append("ctx.lineTo(" + (x + 165) + ", " + (y + 25) + ");\n");
                code.append("ctx.lineTo(" + (x + 165) + ", " + (y + 65) + ");\n");
                code.append("ctx.lineTo(" + (x + 80) + ", " + (y + 65) + ");\n");
                code.append("ctx.lineTo(" + (x + 80) + ", " + (y + 50) + ");\n");
                code.append("ctx.stroke();\n");
                code.append("ctx.fillText('Цикл', " + (x + 140) + "," + (y + 15) + ");\n");
                code.append("\n");
            }
        }


        if (node.getNodeName().equals("then")) {
            code.append("ctx.beginPath();\n");
            code.append("ctx.moveTo(" + (x + 100) + ", " + (y + 25) + ");\n");
            code.append("ctx.lineTo(" + (x + 150) + ", " + (y + 25) + ");\n");
            code.append("ctx.lineTo(" + (x + 150) + ", " + (y + 75) + ");\n");
            code.append("ctx.stroke();\n");
            code.append("ctx.fillText('Да', " + (x + 110) + "," + (y + 15) + ");\n");
            lastX = x + 100;
            lastY = y + 75;
            code.append("\n");
        } else if (node.getNodeName().equals("else")) {
            code.append("ctx.beginPath();\n");
            code.append("ctx.moveTo(" + lastIfX + ", " + (lastIfY + 25) + ");\n");
            code.append("ctx.lineTo(" + (lastIfX - 50) + ", " + (lastIfY + 25) + ");\n");
            code.append("ctx.lineTo(" + (lastIfX - 50) + ", " + (lastIfY + 75) + ");\n");
            code.append("ctx.stroke();\n");
            code.append("ctx.fillText('Нет', " + (lastIfX - 10) + "," + (lastIfY + 15) + ");\n");
            lastX = lastIfX - 100;
            lastY = lastIfY + 75;
            code.append("\n");
        }
//        && (!node.getNodeName().equals("while") && childsWhile == 0)
//                && (!node.getNodeName().equals("for") && childsFor == 0)

        else if (!node.getNodeName().equals("function") && !node.getNodeName().equals("parameters")
                && !node.getNodeName().equals("if") && !node.getNodeName().equals("while")
                && !node.getNodeName().equals("for")) {
            code.append("ctx.beginPath();\n");
            code.append("ctx.moveTo(" + (x + 50) + ", " + (y + 50) + ");\n");
            y += 75;
            code.append("ctx.lineTo(" + (x + 50) + ", " + y + ");\n");
            lastX = x;
            lastY = y;
            code.append("ctx.stroke();\n");
            code.append("\n");
        }
        else if ((node.getNodeName().equals("while") && childsWhile == 0) || (node.getNodeName().equals("for") && childsFor == 0)) {
            code.append("ctx.beginPath();\n");
            code.append("ctx.moveTo(" + (x + 50) + ", " + (y + 50) + ");\n");
            y += 75;
            code.append("ctx.lineTo(" + (x + 50) + ", " + y + ");\n");
            lastX = x;
            lastY = y;
            code.append("ctx.stroke();\n");
            code.append("\n");
        }
        else if ((node.getNodeName().equals("while") && childsWhile > 0) || (node.getNodeName().equals("for") && childsFor > 0)) {
            code.append("ctx.beginPath();\n");
            code.append("ctx.moveTo(" + (x + 115) + ", " + (y + 25) + ");\n");
            code.append("ctx.lineTo(" + (x + 165) + ", " + (y + 25) + ");\n");
            code.append("ctx.lineTo(" + (x + 165) + ", " + (y + 50) + ");\n");
            code.append("ctx.stroke();\n");
            if (node.getNodeName().equals("for")) code.append("ctx.fillText('Конец цикла', " + x + "," + (y + 15) + ");\n");
            else code.append("ctx.fillText('Да', " + (x + 140) + "," + (y + 15) + ");\n");
            lastX += 115;
            lastY += 50;
            code.append("\n");
        }



        // Process child nodes recursively
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE && !child.getNodeName().equals("parameters")) {
                code.append(processNode(child, lastX, lastY));
            }
        }

        if (node.getNodeName().equals("then")) {
            if (childsThen == 0 && childsElse > childsThen) {
                int diff = childsElse - 2;
                code.append("ctx.beginPath();\n");
                code.append("ctx.moveTo(" + (lastX + 50) + ", " + lastY + ");\n");
                code.append("ctx.lineTo(" + (lastX + 50)  + ", " + (lastY + diff * 75) + ");\n");
                code.append("ctx.stroke();\n");
                code.append("\n");
                code.append("ctx.beginPath();\n");
                code.append("ctx.moveTo(" + (lastX + 50) + ", " + (lastY + diff * 75) + ");\n");
                code.append("ctx.lineTo(" + (lastX - 50) + ", " + (lastY + diff * 75) + ");\n");
                code.append("ctx.stroke();\n");
                code.append("\n");
            } else {
                code.append("ctx.beginPath();\n");
                code.append("ctx.moveTo(" + (lastX + 50) + ", " + lastY + ");\n");
                code.append("ctx.lineTo(" + (lastX - 50) + ", " + lastY + ");\n");
                code.append("ctx.stroke();\n");
                code.append("\n");

            }
        } else if (node.getNodeName().equals("else")) {
            if (childsElse == 0 && childsThen > childsElse) {
                int diff = childsThen - 2;
                code.append("ctx.beginPath();\n");
                code.append("ctx.moveTo(" + (lastX + 50) + ", " + lastY + ");\n");
                code.append("ctx.lineTo(" + (lastX + 50) + ", " + (lastY + diff * 75) + ");\n");
                code.append("ctx.stroke();\n");
                code.append("\n");
                code.append("ctx.beginPath();\n");
                code.append("ctx.moveTo(" + (lastX + 50) + ", " + (lastY + diff * 75) + ");\n");
                code.append("ctx.lineTo(" + (lastX + 150) + ", " + (lastY + diff * 75) + ");\n");
                code.append("ctx.stroke();\n");
                code.append("\n");
                lastX += 150;
                lastY = lastY + diff * 75;
            } else {
                code.append("ctx.beginPath();\n");
                code.append("ctx.moveTo(" + (lastX + 50) + ", " + lastY + ");\n");
                code.append("ctx.lineTo(" + (lastX + 150) + ", " + lastY + ");\n");
                code.append("ctx.stroke();\n");
                code.append("\n");
                lastX +=150;
            }
            //стрелочка в конце
            code.append("ctx.beginPath();\n");
            code.append("ctx.moveTo(" + lastX + ", " + lastY + ");\n");
            code.append("ctx.lineTo(" + lastX + ", " + (lastY + 25) + ");\n");
            code.append("ctx.stroke();\n");
            code.append("\n");
            lastX -= 50;
            lastY = lastY + 25;
        }
        else if (node.getNodeName().equals("while") && childsWhile != 0) {
            code.append("ctx.beginPath();\n");
            code.append("ctx.moveTo(" + (lastX + 50) + ", " + lastY + ");\n");
            code.append("ctx.lineTo(" + (lastX - 35) + ", " + lastY + ");\n");
            code.append("ctx.lineTo(" + (lastX - 35) + ", " + (lastY - (75 * (childsWhile / 2))) + ");\n");
            code.append("ctx.stroke();\n");
            code.append("\n");
            code.append("ctx.beginPath();\n");
            code.append("ctx.moveTo(" + (lastX - 65) + ", " + (lastY - (75 * (childsWhile / 2))) + ");\n");
            code.append("ctx.lineTo(" + (lastX - 65) + ", " + (lastY + 25) + ");\n");
            code.append("ctx.stroke();\n");
            code.append("\n");
            lastX -= 115;
            lastY += 25;
            lastNode = "while";
        }
        else if (node.getNodeName().equals("for") && childsFor != 0) {
            code.append("ctx.beginPath();\n");
            code.append("ctx.moveTo(" + (lastX + 50) + ", " + lastY + ");\n");
            code.append("ctx.lineTo(" + (lastX - 35) + ", " + lastY + ");\n");
            code.append("ctx.lineTo(" + (lastX - 35) + ", " + (lastY - (75 * (childsWhile / 2))) + ");\n");
            code.append("ctx.stroke();\n");
            code.append("\n");
            code.append("ctx.beginPath();\n");
            code.append("ctx.moveTo(" + (lastX - 65) + ", " + (lastY - (75 * (childsWhile / 2))) + ");\n");
            code.append("ctx.lineTo(" + (lastX - 65) + ", " + (lastY + 25) + ");\n");
            code.append("ctx.stroke();\n");
            code.append("\n");
            lastX -= 115;
            lastY += 25;
            lastNode = "for";
        }

        if (node.getNodeName().equals("if")) { lastNode = "if";}
        else if (node.getNodeName().equals("body")) {
            code.append("ctx.beginPath();\n");
            code.append("ctx.ellipse(" + (lastX + 50) + ", " + (lastY + 25) + ", 50, 25, 0, 0, Math.PI * 2);\n");
            code.append("ctx.stroke();\n");
            code.append("\n");

            code.append("ctx.fillText('" + "Конец" + "', " + (lastX + 50) + ", " + (lastY + 25) + ");\n");
        }

        return code;
    }
}
