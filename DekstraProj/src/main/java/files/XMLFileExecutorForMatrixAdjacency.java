package files;

import constants.AlertCommands;
import objects.DekstraNode;
import objects.Graph;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class XMLFileExecutorForMatrixAdjacency implements IFileExecutor
{
    private File file = null;

    public XMLFileExecutorForMatrixAdjacency()
    {
    }

    public XMLFileExecutorForMatrixAdjacency(File file)
    {
        this.file = file;
    }

    public AlertCommands fillUp(Graph graph)
    {
        try
        {
            // Создается построитель документа
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            // Создается дерево DOM документа из файла
            Document document = documentBuilder.parse(file);
            // Получаем корневой элемент
            // /graph
            Node rootGraph = document.getDocumentElement();
            NodeList nodes = rootGraph.getChildNodes();

            System.out.println(rootGraph.getNodeName() + " with children: " + rootGraph.getChildNodes() + "with type: " + rootGraph.getNodeType());

            for (int i = 0; i < nodes.getLength(); i++)
            {
                // /graph/node
                Node node = nodes.item(i);
                if (!isElementValid(node, "node", Node.ELEMENT_NODE)) continue;


                //region Node variables
                // /graph/node/@number
                int nodeNumber = Integer.parseInt(node.getAttributes().item(0).getNodeValue());
                List<Integer> nextNodesNumbers = new CopyOnWriteArrayList<>();
                List<Integer> nextNodesWeights = new ArrayList<>();
                //endregion

                NodeList nodeTags = node.getChildNodes();
                for (int j = 0; j < nodeTags.getLength(); j++)
                {
                    Node nodeTag = nodeTags.item(j);
                    // /graph/node/name
                    if (isElementValid(nodeTag, "name", Node.ELEMENT_NODE))
                    {
                        //This tag name is not necessary
                        //but it can be useful in the future
                        int a = 1;
                    }
                    // /graph/node/edges
                    else if (isElementValid(nodeTag, "edges", Node.ELEMENT_NODE))
                    {
                        NodeList edges = nodeTag.getChildNodes();

                        for (int k = 0; k < edges.getLength(); k++)
                        {
                            // /graph/node/edges/edge
                            Node edge = edges.item(k);
                            if (!isElementValid(edge, "edge", Node.ELEMENT_NODE)) continue;

                            // /graph/node/edges/edge/@targetNodeNumber
                            int targetNodeNumber = Integer.parseInt(edge.getAttributes().item(0).getNodeValue());

                            // /graph/node/edges/edge/weight
                            int indexWightNode = 0;
                            Node tagWeight = edge.getChildNodes().item(indexWightNode);
                            while(!isElementValid(tagWeight, "weight", Node.ELEMENT_NODE) && indexWightNode < edge.getChildNodes().getLength()) {
                                indexWightNode++;
                                tagWeight = edge.getChildNodes().item(indexWightNode);
                            }

                            nextNodesNumbers.add(targetNodeNumber);
                            nextNodesWeights.add(Integer.parseInt(tagWeight.getTextContent()));
                        }
                    }
                }

                graph.add(new DekstraNode(new objects.Node(nodeNumber, nextNodesNumbers, nextNodesWeights)));
            }
        }
        catch (ParserConfigurationException e)
        {
            return AlertCommands.ERROR_RESULT;
        }
        catch (SAXException e)
        {
            return AlertCommands.ERROR_RESULT;
        }
        catch (IOException e)
        {
            return AlertCommands.ERROR_RESULT;
        }

        return AlertCommands.RIGHTS_RESULT;
    }

    private boolean isElementValid(Node element, String elementName, short elementType)
    {
        if (element == null) return false;
        if (elementName == null) return false;
        if (elementName.isEmpty()) return false;

        if (element.getNodeType() == elementType)
        {
            if (element.getNodeName().equalsIgnoreCase(elementName))
            {
                return true;
            }
        }

        return false;
    }
}
