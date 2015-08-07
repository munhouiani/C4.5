import com.opencsv.CSVReader;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by mhwong on 8/5/15.
 */
public class DecisionTree {
    boolean printPredictAndActual = false;

    String filePath;
    Dataset dataset;
    Dataset testset;
    String target;
    ArrayList<String> attributeList;

    public DecisionTree(String filePath, String target) {
        this.filePath = filePath;

        this.target = target;

        // create dataset
        attributeList = new ArrayList<>();
        create_dataset_from_file();

        // split dataset to testset and train set
        build_testset();

        // rename dataset
        Dataset trainset = dataset;
        dataset = null;

        // generate decision tree
        Node decisionTree = generate_decision_tree(trainset, attributeList, target);

        // validate decision tree accuracy
        validate_decision_tree(testset, decisionTree, target);

    }

    public DecisionTree() {
        this("dataset/CUSTOMER.TXT", "member_card");
    }

    private void create_dataset_from_file() {

        try {
            FileReader fileReader = new FileReader(filePath);
            CSVReader csvReader = new CSVReader(fileReader);
            dataset = new Dataset();
            testset = new Dataset();

            Column column;
            String attribute;

            // read the first line to add attribute
            String[] splitItem = csvReader.readNext();

            // splitItem[0]: customer_id

            // splitItem[1]: account_num

            // splitItem[2]: lname

            // splitItem[3]: fname

            // splitItem[4]: address

            // splitItem[5]: city
            attribute = splitItem[5].replace("\"", "");
            column = new Column(attribute);
            dataset.addColumn(column);

            column = new Column(attribute);
            testset.addColumn(column);

            // attributeList.add(attribute);


            // splitItem[6]: state_province
            attribute = splitItem[6].replace("\"", "");
            column = new Column(attribute);
            dataset.addColumn(column);

            column = new Column(attribute);
            testset.addColumn(column);

            //attributeList.add(attribute);

            // splitItem[7]: postal_code


            // splitItem[8]: country
            attribute = attribute = splitItem[8].replace("\"", "");
            column = new Column(attribute);
            dataset.addColumn(column);

            column = new Column(attribute);
            testset.addColumn(column);

            if(!attribute.equals(target)){
                attributeList.add(attribute);
            }

            // splitItem[9]: customer_region_id

            // splitItem[10]: phone


            // splitItem[11]: marital_status
            attribute = splitItem[11].replace("\"", "");
            column = new Column(attribute);
            dataset.addColumn(column);

            column = new Column(attribute);
            testset.addColumn(column);

            if(!attribute.equals(target)){
                attributeList.add(attribute);
            }


            // splitItem[12]: gender
            attribute = splitItem[12].replace("\"", "");
            column = new Column(attribute);
            dataset.addColumn(column);

            column = new Column(attribute);
            testset.addColumn(column);

            if(!attribute.equals(target)){
                attributeList.add(attribute);
            }


            // splitItem[13]: total_children
            attribute = splitItem[13].replace("\"", "");
            column = new Column(attribute);
            column.type = "Integer";
            dataset.addColumn(column);

            column = new Column(attribute);
            column.type = "Integer";
            testset.addColumn(column);

            if(!attribute.equals(target)){
                attributeList.add(attribute);
            }


            // splitItem[14]: num_children_at_home
            attribute = splitItem[14].replace("\"", "");
            column = new Column(attribute);
            column.type = "Integer";
            dataset.addColumn(column);

            column = new Column(attribute);
            column.type = "Integer";
            testset.addColumn(column);

            if(!attribute.equals(target)){
                attributeList.add(attribute);
            }


            // splitItem[15]: education
            attribute = splitItem[15].replace("\"", "");
            column = new Column(attribute);
            dataset.addColumn(column);

            column = new Column(attribute);
            testset.addColumn(column);

            if(!attribute.equals(target)){
                attributeList.add(attribute);
            }


            // splitItem[16]: member_card, our targer, not included in attribute_list
            attribute = splitItem[16].replace("\"", "");
            column = new Column(attribute);
            dataset.addColumn(column);

            column = new Column(attribute);
            testset.addColumn(column);

            if(!attribute.equals(target)){
                attributeList.add(attribute);
            }



            // splitItem[17]: age
            attribute = splitItem[17].replace("\"", "");
            column = new Column(attribute);
            column.type = "Integer";
            dataset.addColumn(column);

            column = new Column(attribute);
            column.type = "Integer";
            testset.addColumn(column);

            if(!attribute.equals(target)){
                attributeList.add(attribute);
            }


            // splitItem[18]: year_income
            attribute = splitItem[18].replace("\"", "");
            column = new Column(attribute);
            column.type = "Integer";
            dataset.addColumn(column);

            column = new Column(attribute);
            column.type = "Integer";
            testset.addColumn(column);

            if(!attribute.equals(target)){
                attributeList.add(attribute);
            }

            while((splitItem = csvReader.readNext()) != null) {

                // add city
                dataset.getColumn("city").addValue(splitItem[5].replace("\"", ""));

                // add state_province
                dataset.getColumn("state_province").addValue(splitItem[6].replace("\"", ""));

                // add country
                dataset.getColumn("country").addValue(splitItem[8].replace("\"", ""));

                // add marital_status
                dataset.getColumn("marital_status").addValue(splitItem[11].replace("\"", ""));

                // add gender
                dataset.getColumn("gender").addValue(splitItem[12].replace("\"", ""));

                // add total_children
                dataset.getColumn("total_children").addValue(splitItem[13].replace("\"", ""));

                // add num_children_at_home
                dataset.getColumn("num_children_at_home").addValue(splitItem[14].replace("\"", ""));

                // add education
                dataset.getColumn("education").addValue(splitItem[15].replace("\"", ""));

                // add member_card
                dataset.getColumn("member_card").addValue(splitItem[16].replace("\"", ""));

                // add age
                dataset.getColumn("age").addValue(splitItem[17].replace("\"", ""));

                // add year_income
                dataset.getColumn("year_income").addValue(splitItem[18].replace("\"", ""));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void build_testset() {
        // calculate the ratio of label (member_card)
        HashMap<String, Integer> frequency = new HashMap<>();
        Column column = dataset.getColumn(target);
        for(int i = 0; i < column.getRowSize(); i++) {
            String item = column.getRowValue(i);
            if(frequency.containsKey(item)) {
                frequency.put(item, frequency.get(item)+1);
            }
            else {
                frequency.put(item, 1);
            }
        }

        int randomNumberRange = 0;
        // calculate how many rows should be put into testset accordingly
        for(String key: frequency.keySet()) {
            int localNum = (int) Math.floor((double)frequency.get(key) * 0.3);
            randomNumberRange += localNum;
            frequency.put(key, localNum);
        }

        // randomly get rows
        Random random = new Random(System.currentTimeMillis());
        while(!is_all_key_empty(frequency)) {
            int randomNumber = random.nextInt(randomNumberRange);
            String label = dataset.getColumn(target).getRowValue(randomNumber);
            if(frequency.get(label) != 0) {
                frequency.put(label, frequency.get(label) - 1);
                // add city
                column = dataset.getColumn("city");
                testset.getColumn("city").addValue(column.getRowValue(randomNumber));
                column.row.remove(randomNumber);

                // add state_province
                column = dataset.getColumn("state_province");
                testset.getColumn("state_province").addValue(column.getRowValue(randomNumber));
                column.row.remove(randomNumber);

                // add country
                column = dataset.getColumn("country");
                testset.getColumn("country").addValue(column.getRowValue(randomNumber));
                column.row.remove(randomNumber);

                // add marital_status
                column = dataset.getColumn("marital_status");
                testset.getColumn("marital_status").addValue(column.getRowValue(randomNumber));
                column.row.remove(randomNumber);

                // add gender
                column = dataset.getColumn("gender");
                testset.getColumn("gender").addValue(column.getRowValue(randomNumber));
                column.row.remove(randomNumber);

                // add total_children
                column = dataset.getColumn("total_children");
                testset.getColumn("total_children").addValue(column.getRowValue(randomNumber));
                column.row.remove(randomNumber);

                // add num_children_at_home
                column = dataset.getColumn("num_children_at_home");
                testset.getColumn("num_children_at_home").addValue(column.getRowValue(randomNumber));
                column.row.remove(randomNumber);

                // add education
                column = dataset.getColumn("education");
                testset.getColumn("education").addValue(column.getRowValue(randomNumber));
                column.row.remove(randomNumber);


                // add member_card
                column = dataset.getColumn("member_card");
                testset.getColumn("member_card").addValue(column.getRowValue(randomNumber));
                column.row.remove(randomNumber);

                // add age
                column = dataset.getColumn("age");
                testset.getColumn("age").addValue(column.getRowValue(randomNumber));
                column.row.remove(randomNumber);

                // add year_income
                column = dataset.getColumn("year_income");
                testset.getColumn("year_income").addValue(column.getRowValue(randomNumber));
                column.row.remove(randomNumber);
            }
        }

    }

    private boolean is_all_key_empty(HashMap<String, Integer> map) {
        boolean result = true;
        for(String key: map.keySet()) {
            if(map.get(key) != 0) {
                result = false;
                break;
            }
        }
        return result;
    }

    private Node generate_decision_tree(Dataset dataset, ArrayList<String> attributeList, String target) {
        // create a node N
        Node node = new Node();

        HashMap<String, Integer> frequency = dataset.getColumn(target).getFrequencyTable();

        // if tuple in D is same class, return N as a leaf node with the class C
        if(frequency.keySet().size() == 1) {
            node.attribute = frequency.keySet().iterator().next();
            return node;
        }

        // if the attribute list is empty, return N as a leaf node with the majority class
        if(attributeList.isEmpty()) {
            String label = "";
            int currentCount = 0;
            for(String key: frequency.keySet()) {
                if(frequency.get(key) > currentCount) {
                    label = key;
                    currentCount = frequency.get(key);
                }
            }
            node.attribute = label;
            return node;
        }

        // apply attribute selection method
        ArrayList<Dataset> subDatasetList = new ArrayList<>();
        String best_attribute = measure_with_information_gain(dataset, attributeList, subDatasetList, target);
        String[] token = best_attribute.split(" ");
        if(!best_attribute.isEmpty()) {
            attributeList.remove(token[1]);
            if(token[0].equals("Integer")) {
                node.attribute = "Integer " + token[1];

                for(Dataset subDataset: subDatasetList) {
                    Node child = generate_decision_tree(subDataset, attributeList, target);
                    if(Double.parseDouble(subDataset.getColumn(token[1]).getRowValue(0)) <= Double.parseDouble(token[2])) {
                        child.pathName = "<= " + token[2];
                    }
                    else {
                        child.pathName = "> " + token[2];
                    }
                    node.child.add(child);
                }
            }
            else {
                node.attribute = "String " + token[1];
                for(Dataset subDataset: subDatasetList) {

                    Node child = generate_decision_tree(subDataset, attributeList, target);
                    child.pathName = subDataset.getColumn(token[1]).getRowValue(0);
                    node.child.add(child);
                }
            }
        }


        return node;

    }

    private String measure_with_information_gain(Dataset dataset, ArrayList<String> attributeList, ArrayList<Dataset> subDatasetList, String target) {

        String bestAttribute = "";
        double currentGain = 0.0;
        // count the information gain of original data
        double currentInformation = count_information_gain_of_dataset(dataset, target);

        // split data by every attribute in attribute list and count entropy

        for(String attribute: attributeList) {
            if(dataset.getColumn(attribute).type.equals("String")) {
                String splitAttribute = "String " + attribute;
                ArrayList<Dataset> splittedDatasetList = split_dataset_by_attribute(dataset, splitAttribute);
                double entropy = 0.0;
                for(Dataset splitdataset: splittedDatasetList) {
                    entropy += (double)splitdataset.getRowSize()/(double)dataset.getRowSize() * count_information_gain_of_dataset(splitdataset, target);
                }
                double localGain = currentInformation - entropy;
                if(localGain >= currentGain) {
                    currentGain = localGain;
                    bestAttribute = splitAttribute;
                    // clone splitted dataset list
                    subDatasetList.clear();
                    for(Dataset tempDataset: splittedDatasetList) {
                        Dataset cloneDataset = new Dataset(tempDataset);
                        subDatasetList.add(cloneDataset);
                    }

                }
            }
            else {      // integer type
                Column attributeColumn = dataset.getColumn(attribute);
                // get a sorted array list from integer column
                ArrayList<String> sortedAttributeList = new ArrayList<>(attributeColumn.getSetofValue());
                ArrayList<Double> splitAttributeItemList = new ArrayList<>();
                if(sortedAttributeList.size() == 1) {
                    double item = Double.parseDouble(sortedAttributeList.get(0));
                    splitAttributeItemList.add(item);
                }
                else {
                    for(int i = 0; i < sortedAttributeList.size() - 1; i++) {
                        double current = Double.parseDouble(sortedAttributeList.get(i));
                        double next = Double.parseDouble(sortedAttributeList.get(i + 1));
                        double average = (current + next) / 2;
                        splitAttributeItemList.add(average);
                    }
                }

                double attributeGain = 0.0;
                String localAttribute = "";
                ArrayList<Dataset> localSubDataList = null;
                // split by every item in split attribute item list
                for(double attributeItem: splitAttributeItemList) {
                    String splitAttribute = "Integer " + attribute + " " + attributeItem;
                    ArrayList<Dataset> splittedDatasetList = split_dataset_by_attribute(dataset, splitAttribute);
                    double entropy = 0.0;
                    for(Dataset splitdataset: splittedDatasetList) {
                        entropy += (double)splitdataset.getRowSize()/(double)dataset.getRowSize() * count_information_gain_of_dataset(splitdataset, target);
                    }
                    double localGain = currentInformation - entropy;
                    if(localGain >= attributeGain) {
                        attributeGain = localGain;
                        localAttribute = splitAttribute;
                        localSubDataList = splittedDatasetList;
                    }

                }
                if(attributeGain >= currentGain) {
                    currentGain = attributeGain;
                    bestAttribute = localAttribute;
                    // clone splitted dataset list
                    subDatasetList.clear();
                    for(Dataset tempDataset: localSubDataList) {
                        Dataset cloneDataset = new Dataset(tempDataset);
                        subDatasetList.add(cloneDataset);
                    }
                }
            }

        }

        return bestAttribute;
    }

    private String measure_with_gini_index(Dataset dataset, ArrayList<String> attributeList, ArrayList<Dataset> subDatasetList, String target) {
        String bestAttribute = "";

        // count the gini of the original data
        double currentGini = count_gini_of_column(dataset.getColumn(target));

        // split data by every attribute in attribute list and count gini

        for(String attribute: attributeList) {
            if(dataset.getColumn(attribute).type.equals("String")) {
                String splitAttribute = "String " + attribute;
                ArrayList<Dataset> splittedDatasetList = split_dataset_by_attribute(dataset, splitAttribute);
                double attributeGini = 0.0;
                for(Dataset splitdataset: splittedDatasetList) {
                    double splitGini = count_gini_of_column(splitdataset.getColumn(target));
                    attributeGini = splitGini * splitdataset.getRowSize()/dataset.getRowSize();
                }

                if(attributeGini <= currentGini) {
                    currentGini = attributeGini;
                    bestAttribute = splitAttribute;
                    // clone splitted dataset list
                    subDatasetList.clear();
                    for(Dataset tempDataset: splittedDatasetList) {
                        Dataset cloneDataset = new Dataset(tempDataset);
                        subDatasetList.add(cloneDataset);
                    }

                }
            }
            else {      // integer type
                Column attributeColumn = dataset.getColumn(attribute);
                // get a sorted array list from integer column
                ArrayList<String> sortedAttributeList = new ArrayList<>(attributeColumn.getSetofValue());
                ArrayList<Double> splitAttributeItemList = new ArrayList<>();
                if(sortedAttributeList.size() == 1) {
                    double item = Double.parseDouble(sortedAttributeList.get(0));
                    splitAttributeItemList.add(item);
                }
                else {
                    for(int i = 0; i < sortedAttributeList.size() - 1; i++) {
                        double current = Double.parseDouble(sortedAttributeList.get(i));
                        double next = Double.parseDouble(sortedAttributeList.get(i+1));
                        double average = (current + next) / 2;
                        splitAttributeItemList.add(average);
                    }
                }

                double attributeGini = Double.MAX_VALUE;
                String localAttribute = "";
                ArrayList<Dataset> localSubDataList = null;
                // split by every item in split attribute item list
                for(double attributeItem: splitAttributeItemList) {
                    String splitAttribute = "Integer " + attribute + " " + attributeItem;
                    ArrayList<Dataset> splittedDatasetList = split_dataset_by_attribute(dataset, splitAttribute);
                    double localGini = 0.0;
                    for(Dataset splitdataset: splittedDatasetList) {
                        double splitGini = count_gini_of_column(splitdataset.getColumn(target));
                        localGini = splitGini * splitdataset.getRowSize()/dataset.getRowSize();
                    }

                    if(localGini <= attributeGini) {
                        attributeGini = localGini;
                        localAttribute = splitAttribute;
                        localSubDataList = splittedDatasetList;
                    }

                }
                if(attributeGini <= currentGini) {
                    currentGini = attributeGini;
                    bestAttribute = localAttribute;
                    // clone splitted dataset list
                    subDatasetList.clear();
                    for(Dataset tempDataset: localSubDataList) {
                        Dataset cloneDataset = new Dataset(tempDataset);
                        subDatasetList.add(cloneDataset);
                    }
                }
            }

        }

        return bestAttribute;
    }

    private double count_gini_of_column(Column column) {
        TreeSet<String> setofValue = column.getSetofValue();
        double totalDatasize = column.getRowSize();
        double p = 0.0d;
        for(String item: setofValue) {
            double classValue = column.getValueCount(item);
            p += (classValue/totalDatasize) * (classValue/totalDatasize);
        }
        double gini = 1 - p;
        return gini;
    }

    ArrayList<Dataset> split_dataset_by_attribute(Dataset dataset, String best_attribute) {
        ArrayList<Dataset> datasetList = new ArrayList<>();
        String[] token = best_attribute.split(" ");
        if(token[0].equals("String")) {
            String attribute = token[1];
            Column attributeColumn = dataset.getColumn(attribute);
            TreeSet<String> attributeSet = attributeColumn.getSetofValue();
            for(String attributeItem: attributeSet) {
                // clone a dataset with columns only
                Dataset cloneDataset = dataset.cloneDatasetWithColumns();
                for(int i = 0; i < dataset.getRowSize(); i++) {
                    if(attributeColumn.getRowValue(i).equals(attributeItem)) {
                        // add city
                        cloneDataset.getColumn("city").addValue(dataset.getColumn("city").getRowValue(i));

                        // add state_province
                        cloneDataset.getColumn("state_province").addValue(dataset.getColumn("state_province").getRowValue(i));

                        // add country
                        cloneDataset.getColumn("country").addValue(dataset.getColumn("country").getRowValue(i));

                        // add marital_status
                        cloneDataset.getColumn("marital_status").addValue(dataset.getColumn("marital_status").getRowValue(i));

                        // add gender
                        cloneDataset.getColumn("gender").addValue(dataset.getColumn("gender").getRowValue(i));

                        // add total_children
                        cloneDataset.getColumn("total_children").addValue(dataset.getColumn("total_children").getRowValue(i));

                        // add num_children_at_home
                        cloneDataset.getColumn("num_children_at_home").addValue(dataset.getColumn("num_children_at_home").getRowValue(i));

                        // add education
                        cloneDataset.getColumn("education").addValue(dataset.getColumn("education").getRowValue(i));

                        // add member_card
                        cloneDataset.getColumn("member_card").addValue(dataset.getColumn("member_card").getRowValue(i));

                        // add age
                        cloneDataset.getColumn("age").addValue(dataset.getColumn("age").getRowValue(i));

                        // add year_income
                        cloneDataset.getColumn("year_income").addValue(dataset.getColumn("year_income").getRowValue(i));
                    }
                }
                datasetList.add(cloneDataset);
            }
        }
        else {
            String attribute = token[1];
            double threshold = Double.parseDouble(token[2]);
            Column attributeColumn = dataset.getColumn(attribute);

            // clone two datasets with columns only
            Dataset lesserDataset = dataset.cloneDatasetWithColumns();
            Dataset largerDataset = dataset.cloneDatasetWithColumns();

            for(int i = 0; i < dataset.getRowSize(); i++) {
                if(Double.parseDouble(attributeColumn.getRowValue(i)) <= threshold) {
                    // add city
                    lesserDataset.getColumn("city").addValue(dataset.getColumn("city").getRowValue(i));

                    // add state_province
                    lesserDataset.getColumn("state_province").addValue(dataset.getColumn("state_province").getRowValue(i));

                    // add country
                    lesserDataset.getColumn("country").addValue(dataset.getColumn("country").getRowValue(i));

                    // add marital_status
                    lesserDataset.getColumn("marital_status").addValue(dataset.getColumn("marital_status").getRowValue(i));

                    // add gender
                    lesserDataset.getColumn("gender").addValue(dataset.getColumn("gender").getRowValue(i));

                    // add total_children
                    lesserDataset.getColumn("total_children").addValue(dataset.getColumn("total_children").getRowValue(i));

                    // add num_children_at_home
                    lesserDataset.getColumn("num_children_at_home").addValue(dataset.getColumn("num_children_at_home").getRowValue(i));

                    // add education
                    lesserDataset.getColumn("education").addValue(dataset.getColumn("education").getRowValue(i));

                    // add member_card
                    lesserDataset.getColumn("member_card").addValue(dataset.getColumn("member_card").getRowValue(i));

                    // add age
                    lesserDataset.getColumn("age").addValue(dataset.getColumn("age").getRowValue(i));

                    // add year_income
                    lesserDataset.getColumn("year_income").addValue(dataset.getColumn("year_income").getRowValue(i));
                }
                else {
                    // add city
                    largerDataset.getColumn("city").addValue(dataset.getColumn("city").getRowValue(i));

                    // add state_province
                    largerDataset.getColumn("state_province").addValue(dataset.getColumn("state_province").getRowValue(i));

                    // add country
                    largerDataset.getColumn("country").addValue(dataset.getColumn("country").getRowValue(i));

                    // add marital_status
                    largerDataset.getColumn("marital_status").addValue(dataset.getColumn("marital_status").getRowValue(i));

                    // add gender
                    largerDataset.getColumn("gender").addValue(dataset.getColumn("gender").getRowValue(i));

                    // add total_children
                    largerDataset.getColumn("total_children").addValue(dataset.getColumn("total_children").getRowValue(i));

                    // add num_children_at_home
                    largerDataset.getColumn("num_children_at_home").addValue(dataset.getColumn("num_children_at_home").getRowValue(i));

                    // add education
                    largerDataset.getColumn("education").addValue(dataset.getColumn("education").getRowValue(i));

                    // add member_card
                    largerDataset.getColumn("member_card").addValue(dataset.getColumn("member_card").getRowValue(i));

                    // add age
                    largerDataset.getColumn("age").addValue(dataset.getColumn("age").getRowValue(i));

                    // add year_income
                    largerDataset.getColumn("year_income").addValue(dataset.getColumn("year_income").getRowValue(i));
                }
            }
            datasetList.add(lesserDataset);
            datasetList.add(largerDataset);
        }

        return datasetList;
    }

    private double count_information_gain_of_dataset(Dataset dataset, String target) {
        double informationGain = 0.0d;

        // I(p,n) = -(p/(p+n)) lg(p/p+n) - (n/(p+n)) lg(n/(p+n))

        // get the size of dataset
        double totalDataSize = dataset.getRowSize();

        // get the target column
        Column targetColumn = dataset.getColumn(target);

        // get the target's frequency table
        HashMap<String, Integer> targetFrequencyTable = targetColumn.getFrequencyTable();

        // count the Information
        for(String item: targetFrequencyTable.keySet()) {
            double singleItem = targetFrequencyTable.get(item);
            double firstPart = singleItem/totalDataSize;                // the p/(p+n) part
            double secondPart = Math.log(firstPart)/ Math.log(2.0d);    // the lg(p/(p+n)) part
            informationGain -= firstPart * secondPart;
        }

        return informationGain;

    }

    private void validate_decision_tree(Dataset testset, Node decisionTree, String target) {
        double accuracy = 0.0d;
        double correct = 0.0d;
        double incorrect = 0.0d;

        double bac = 0.0d;

        Column targetColumn = testset.getColumn(target);
        TreeSet<String> setofValue = targetColumn.getSetofValue();
        HashMap<String, Double> correctMap = new HashMap<>();
        HashMap<String, Double> totalMap = new HashMap<>();
        for(String item: setofValue) {
            correctMap.put(item, 0.0);
            totalMap.put(item, 0.0);
        }

        for(int i = 0; i < testset.getRowSize(); i++) {
            Node pointer = decisionTree;
            while(!pointer.child.isEmpty()) {
                String attribute = pointer.attribute;
                String[] token = attribute.split(" ");
                if(token[0].equals("Integer")) {
                    Column testColumn = testset.getColumn(token[1]);
                    double testValue = Double.parseDouble(testColumn.getRowValue(i));
                    for(Node child: pointer.child) {
                        String pathName = child.pathName;
                        String[] pathToken = pathName.split(" ");
                        if(pathToken[0].equals("<=") && testValue <= Double.parseDouble(pathToken[1])) {
                            pointer = child;
                            break;
                        }
                        else if(pathToken[0].equals(">") && testValue > Double.parseDouble(pathToken[1])) {
                            pointer = child;
                            break;
                        }
                    }
                }
                else {
                    Column testColumn = testset.getColumn(token[1]);
                    String testValue = testColumn.getRowValue(i);
                    for(Node child: pointer.child) {
                        if(child.pathName.equals(testValue)) {
                            pointer = child;
                            break;
                        }
                    }
                }

            }
            String label = pointer.attribute;
            if(printPredictAndActual){
                System.out.printf("%d %s\t%s\n", i, label, testset.getColumn(target).getRowValue(i));
            }
            if(label.equals(testset.getColumn(target).getRowValue(i))) {
                correct++;
                correctMap.put(label, correctMap.get(label) + 1.0d);
                totalMap.put(label, totalMap.get(label) + 1.0d);
            }
            else {
                incorrect++;
                totalMap.put(testset.getColumn(target).getRowValue(i), totalMap.get(testset.getColumn(target).getRowValue(i)) + 1);
            }
        }

        accuracy = correct / (correct + incorrect) * 100;
        for(String key: correctMap.keySet()) {
            bac += (correctMap.get(key) / totalMap.get(key));
        }

        bac = bac / correctMap.size() * 100;
        System.out.printf("Accuracy: %f\nBAC: %f\n", accuracy, bac);

    }
}
