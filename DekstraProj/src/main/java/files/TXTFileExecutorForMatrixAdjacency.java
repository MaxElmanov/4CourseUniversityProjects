package files;

import constants.AlertCommands;
import constants.Constants;
import objects.DekstraNode;
import objects.Graph;
import objects.Node;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TXTFileExecutorForMatrixAdjacency implements IFileExecutor
{
    private File file = null;

    public TXTFileExecutorForMatrixAdjacency() {}

    public TXTFileExecutorForMatrixAdjacency(File file)
    {
        this.file = file;
    }

    public AlertCommands fillUp(Graph graph)
    {
        List<String> stringList = getContent();

        if(stringList.isEmpty() || stringList.size() == 0) {
            return AlertCommands.ERROR_RESULT;
        }

        try {
            for (int i = 0; i < stringList.size(); i++) {
                String oneStr = stringList.get(i);

                //comment in string
                if (oneStr.contains("--")) continue;

                //form a node with data
                int colonPosition = oneStr.indexOf(":");
                Integer nodeNumber = null;

                if (colonPosition != -1) {
                    String stringNumber = String.valueOf(oneStr.substring(0, colonPosition));
                    nodeNumber = Integer.parseInt(stringNumber);
                }
                else {
                    nodeNumber = i;
                }

                List<Integer> nextNodes = new ArrayList<>();

                List<Integer> nextNodesWeights = new ArrayList<>();

                oneStr = oneStr.replace(" ", "");
                oneStr = oneStr.substring(oneStr.indexOf(":") + 1);
                String[] splittedString = oneStr.split(",");

                for (int j = 0; j < splittedString.length; j++) {
                    int nextNodeWeight = Integer.parseInt(splittedString[j]);

                    if (nextNodeWeight > 0) {
                        nextNodes.add(j + 1);
                        nextNodesWeights.add(nextNodeWeight);
                    }
                }

                if (nextNodes.isEmpty() || nextNodesWeights.isEmpty()) {
                    graph.add(new DekstraNode(new Node(nodeNumber, null, null)));
                }
                else {
                    graph.add(new DekstraNode(new Node(nodeNumber, nextNodes, nextNodesWeights)));
                }
            }
        }
        catch(Exception e){
            //e.printStackTrace();
            return AlertCommands.ERROR_RESULT;
        }

        return AlertCommands.RIGHTS_RESULT;
    }

    private List<String> getContent()
    {
        List<String> stringList = new ArrayList<>();

        try (FileReader fileReader = new FileReader(file)) {
            BufferedReader reader = new BufferedReader(fileReader);
            String str = null;
            int lineNumber = 1;
            while ((str = reader.readLine()) != null && lineNumber <= Constants.MAX_GENERATED_NUMBER) {
                stringList.add(str);
                lineNumber++;
            }
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return stringList;
    }

    private File getFileFromResources(String fileName) {

        ClassLoader classLoader = getClass().getClassLoader();

        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            return new File(resource.getFile());
        }

    }
}
