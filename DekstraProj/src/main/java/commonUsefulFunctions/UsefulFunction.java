package commonUsefulFunctions;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import objects.DekstraNode;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class UsefulFunction
{
    public static void throwException(String message)
    {
        try {
            throw new Exception(message);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            System.exit(-1);
        }
    }

    /**
     * K - Integer, V - List<Integer>>
     **/
    public static <K, V> void fillUpMap(Map<K, List<V>> map, K key, V newValue)
    {
        if (map.isEmpty()) {
            map.put(key, Arrays.asList(newValue));
            return;
        }

        if (map.containsKey(key)) {
            //for case when all "pathNumbers" is filled up in the map
            for (Map.Entry<K, List<V>> entry : map.entrySet()) {
                if (entry.getKey() == key) {
                    List<V> tempList = new ArrayList<>();

                    for (V v : entry.getValue()) {
                        tempList.add(v);
                    }

                    tempList.add(newValue);

                    map.put(key, tempList);
                    return;
                }
            }
        }
        else {
            //for case when there is opportunity that map doesn't contain the key
            map.put(key, Arrays.asList(newValue));
        }
    }

    public static void fillUpMapForManyParents(Map<Integer, List<Integer>> map, int fromIndex, int newValue, int toIndex)
    {
        if (map.isEmpty()) {
            for (int key = fromIndex; key < toIndex; key++) {
                map.put(key, Arrays.asList(newValue));
            }
        }
        else {
            for (Map.Entry<Integer, List<Integer>> entry : map.entrySet()) {
                int key = entry.getKey();
                if (key >= fromIndex && key < toIndex) {
                    List<Integer> tempList = new ArrayList<>();

                    for (Integer v : entry.getValue()) {
                        tempList.add(v);
                    }

                    tempList.add(newValue);

                    map.put(key, tempList);
                }
            }
        }
    }

    public static void fillUpMapByList(Map<Integer, List<Integer>> map, int pathNumber, List<Integer> paths)
    {
        List<Integer> tempList = new ArrayList<>();

        for (Integer numberFromMap : map.get(pathNumber)) {
            tempList.add(numberFromMap);
        }

        for (Integer numberFromPaths : paths) {
            if (!tempList.contains(numberFromPaths)) {
                tempList.add(numberFromPaths);
            }
        }

        map.put(pathNumber, tempList);
    }

    public static void fillUpMapByReverseList(Map<Integer, List<Integer>> map, int pathNumber, List<Integer> paths)
    {
        List<Integer> tempList = map.get(pathNumber);

        for (int i = paths.size() - 1; i >= 0; i--) {
            int number = paths.get(i);

            if (!tempList.contains(number)) {
                tempList.add(number);
            }
        }

        map.put(pathNumber, tempList);
    }

    public static void printMap(Map<Integer, List<Integer>> map)
    {
        for (Map.Entry<Integer, List<Integer>> entry : map.entrySet()) {
            System.out.print("№" + (entry.getKey() + 1) + ": "); // +1 because index commence from zero [0]
            for (Integer nodeNumber : entry.getValue()) {
                System.out.print(nodeNumber + "➔");
            }
            System.out.println();
        }
    }

    public static String getMapContent(Map<Integer, List<Integer>> map)
    {

        StringBuilder builder = new StringBuilder();

        for (Map.Entry<Integer, List<Integer>> entry : map.entrySet()) {
            builder.append("№ " + (entry.getKey() + 1) + ": "); // +1 because index commence from zero [0]
            for (Integer nodeNumber : entry.getValue()) {
                builder.append(nodeNumber + "➔");
            }
            builder = new StringBuilder(builder.substring(0, builder.length() - 1));
            builder.append(System.lineSeparator());
        }

        return builder.toString();
    }

    public static <T> void printList(List<T> list)
    {
        for (T nodeNumber : list) {
            System.out.print(nodeNumber + "->");
        }
        System.out.println();
    }

    public static boolean isLastIndex(int index, List<Integer> parentNodesNumbers)
    {
        return (parentNodesNumbers.size() - 1) == index
               ? true
               : false;
    }

    public static void removeExistingItemFromListByIndex(List<Integer> paths, Integer element)
    {
        if (paths.contains(element)) {
            paths.remove(element);
        }
    }

    public static void removeExistingItemFromListByIndex(List<Integer> paths, Integer element, List<Integer> besidesList)
    {
        if (besidesList.contains(element)) {
            return;
        }

        //invokes this method if element doesn't equal every item of "besidesList" which consists of startPoint and EndPoint
        removeExistingItemFromListByIndex(paths, element);
    }

    public static int getExistingElementIndexIn(List<Integer> listOfMap, DekstraNode node)
    {
        for (int i = 0; i < listOfMap.size(); i++) {
            if (listOfMap.get(i) == node.getNumber()) {
                return i;
            }
        }

        return -1;
    }

    public static boolean listContainsElement(List<Integer> list, int element)
    {
        if (list.contains(element)) {
            return true;
        }

        return false;
    }

    public static boolean listContainsAllElements(List<Integer> listToSearch, List<Integer> listWithElements)
    {
        if (listToSearch == null || listToSearch.isEmpty() || listWithElements == null || listWithElements.isEmpty()) {
            return false;
        }

        for (Integer nextNodeNumber : listWithElements) {
            if (!listToSearch.contains(nextNodeNumber)) {
                return false;
            }
        }

        return true;
    }

    public synchronized static Integer generateNewPathNumberOf(Map<Integer, List<Integer>> map, List<Integer> listOfUsedPathNumbers)
    {
        for (Map.Entry<Integer, List<Integer>> entry : map.entrySet()) {
            Integer key = entry.getKey();
            if (!listOfUsedPathNumbers.contains(key)) {
                return key;
            }
        }

        return null;
    }

    public static Integer generateNewPathNumberRangeFrom0To(Integer amountAllBackPaths, List<Integer> listOfUsedPathNumbers)
    {
        for (int i = 0; i <= amountAllBackPaths; i++) {
            if (!listOfUsedPathNumbers.contains(i)) {
                return i;
            }
        }

        return null;
    }

    public static void fillUpListByReverseList(List<Integer> list, List<Integer> listToAdd)
    {
        if (listToAdd == null || listToAdd.isEmpty()) return;

        if (list == null || list.isEmpty()) {
            for (int i = listToAdd.size() - 1; i >= 0; i--) {
                list.add(listToAdd.get(i));
            }
        }
        else {
            for (int i = listToAdd.size() - 1; i >= 0; i--) {
                list.add(listToAdd.get(i));
            }
        }
    }

    public static <T> void fillUpListByList(List<T> list, List<T> listToAdd)
    {
        if (listToAdd == null || listToAdd.isEmpty()) return;

        if (list == null || list.isEmpty()) {
            for (int i = 0; i < listToAdd.size(); i++) {
                list.add(listToAdd.get(i));
            }
        }
        else {
            list.addAll(listToAdd);
        }
    }

    public static List<Integer> getListElementsWhichExistIn(List<Integer> listWithNecessaryElements, List<Integer> list)
    {
        List<Integer> tempList = new CopyOnWriteArrayList<>();
        for(Integer value : list){
            if(UsefulFunction.listContainsElement(listWithNecessaryElements, value)) {
                tempList.add(value);
            }
        }

        return tempList;
    }

    public static void removeCanvasObjectsByID(Pane canvas, String IdToRemove){
        if (canvas == null) return;
        if (canvas.getChildren() == null) return;
        if (canvas.getChildren().isEmpty()) return;
        if (IdToRemove == null) return;
        if (IdToRemove.isEmpty()) return;

        ObservableList<Node> gridNodesList = canvas.getChildren();

        //region Clear UI objects
        boolean nodeIdMustBeRemoved = false;
        ObservableList<Node> newGridNodesList = FXCollections.observableArrayList();
        for (Node node : gridNodesList) {
            String node_ID = node.getId();

            if (IdToRemove.equalsIgnoreCase(node_ID)) {
                nodeIdMustBeRemoved = true;
                break;
            }

            if(nodeIdMustBeRemoved == false) {
                newGridNodesList.add(node);
            }

            nodeIdMustBeRemoved = false;
        }

        gridNodesList.clear();

        for (Node savedNode : newGridNodesList) {
            gridNodesList.add(savedNode);
        }
        //endregion
    }

    public static List<String> getStringListInsteadOfStringBuilderList(List<StringBuilder> stringBuilderList)
    {
        List<String> stringList = new ArrayList<>();

        for (StringBuilder sb : stringBuilderList){
            stringList.add(sb.toString());
        }

        return stringList;
    }
}
