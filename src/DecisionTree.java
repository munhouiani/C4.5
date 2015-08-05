import com.opencsv.CSVReader;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by mhwong on 8/5/15.
 */
public class DecisionTree {
    String filePath;
    Dataset dataset;
    Dataset testset;
    ArrayList<String> attributeList;

    public DecisionTree(String filePath) {
        this.filePath = filePath;

        // create dataset
        attributeList = new ArrayList<>();
        create_dataset_from_file();

        // split dataset to testset and train set
        build_testset();

        // rename dataset
        Dataset trainset = dataset;
        dataset = null;

        // generate decision tree
        generate_decision_tree(trainset, attributeList);
    }

    public DecisionTree() {
        this("/home/mhwong/Desktop/c4.5_dataset/CUSTOMER.TXT");
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

            attributeList.add(attribute);


            // splitItem[6]: state_province
            attribute = splitItem[6].replace("\"", "");
            column = new Column(attribute);
            dataset.addColumn(column);

            column = new Column(attribute);
            testset.addColumn(column);

            attributeList.add(attribute);

            // splitItem[7]: postal_code


            // splitItem[8]: country
            attribute = attribute = splitItem[8].replace("\"", "");
            column = new Column(attribute);
            dataset.addColumn(column);

            column = new Column(attribute);
            testset.addColumn(column);

            attributeList.add(attribute);

            // splitItem[9]: customer_region_id

            // splitItem[10]: phone


            // splitItem[11]: marital_status
            attribute = splitItem[11].replace("\"", "");
            column = new Column(attribute);
            dataset.addColumn(column);

            column = new Column(attribute);
            testset.addColumn(column);

            attributeList.add(attribute);


            // splitItem[12]: gender
            attribute = splitItem[12].replace("\"", "");
            column = new Column(attribute);
            dataset.addColumn(column);

            column = new Column(attribute);
            testset.addColumn(column);

            attributeList.add(attribute);


            // splitItem[13]: total_children
            attribute = splitItem[13].replace("\"", "");
            column = new Column(attribute);
            column.type = "Integer";
            dataset.addColumn(column);

            column = new Column(attribute);
            column.type = "Integer";
            testset.addColumn(column);

            attributeList.add(attribute);


            // splitItem[14]: num_children_at_home
            attribute = splitItem[14].replace("\"", "");
            column = new Column(attribute);
            column.type = "Integer";
            dataset.addColumn(column);

            column = new Column(attribute);
            column.type = "Integer";
            testset.addColumn(column);

            attributeList.add(attribute);


            // splitItem[15]: education
            attribute = splitItem[15].replace("\"", "");
            column = new Column(attribute);
            dataset.addColumn(column);

            column = new Column(attribute);
            testset.addColumn(column);

            attributeList.add(attribute);


            // splitItem[16]: member_card
            attribute = splitItem[16].replace("\"", "");
            column = new Column(attribute);
            dataset.addColumn(column);

            column = new Column(attribute);
            testset.addColumn(column);

            attributeList.add(attribute);


            // splitItem[17]: age
            attribute = splitItem[17].replace("\"", "");
            column = new Column(attribute);
            column.type = "Integer";
            dataset.addColumn(column);

            column = new Column(attribute);
            column.type = "Integer";
            testset.addColumn(column);

            attributeList.add(attribute);


            // splitItem[18]: year_income
            attribute = splitItem[18].replace("\"", "");
            column = new Column(attribute);
            column.type = "Integer";
            dataset.addColumn(column);

            column = new Column(attribute);
            column.type = "Integer";
            testset.addColumn(column);

            attributeList.add(attribute);

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
        Column column = dataset.getColumn("member_card");
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
            String label = dataset.getColumn("member_card").getRowValue(randomNumber);
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

    private Node generate_decision_tree(Dataset dataset, ArrayList<String> attributeList) {
        // create a node N
        Node node = new Node();

        HashMap<String, Integer> frequency = dataset.getColumn("member_card").getFrequencyTable();

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
        String best_attribute = informationGain(dataset, attributeList);
        return node;

    }

    private String informationGain(Dataset dataset, ArrayList<String> attributeList) {
        // first count current dataset information
        double currentInformation = 0.0;
        HashMap<String, Integer> targetFrequencyTable = dataset.getColumn("member_card").getFrequencyTable();
        double totaldata = dataset.getRowSize();
        for(String item: targetFrequencyTable.keySet()) {
            currentInformation -= (targetFrequencyTable.get(item)) / (totaldata) * (Math.log((targetFrequencyTable.get(item)) / (totaldata)) / Math.log(2.0d));
        }

        double gain = 0.0;
        String bestAttribute = "";
        for(String attribute: attributeList) {

            Column attributeColumn = dataset.getColumn(attribute);
            HashMap<String, Integer> attributeFrequencyTable = attributeColumn.getFrequencyTable();
            Column labelColumn = dataset.getColumn("member_card");
            HashMap<String, Integer> labelFrequencyTable = labelColumn.getFrequencyTable();

            // build a attribute to label hash map
            HashMap<String, HashMap<String, Integer>> attributeToLabel = new HashMap<>();
            for(String key: attributeFrequencyTable.keySet()) {
                // build a label hashmap first
                HashMap<String, Integer> labelHashMap = new HashMap<>();
                for(String label: labelFrequencyTable.keySet()) {
                    labelHashMap.put(label, 0);
                }
                attributeToLabel.put(key, labelHashMap);
            }

            // count attribute to label
            for(int i = 0; i < attributeColumn.getRowSize(); i++) {
                String localAttribute = attributeColumn.getRowValue(i);
                String localLabel = labelColumn.getRowValue(i);
                HashMap<String, Integer> localLabelFrequency = attributeToLabel.get(localAttribute);
                localLabelFrequency.put(localLabel, localLabelFrequency.get(localLabel)+1);
                attributeToLabel.put(localAttribute, localLabelFrequency);
            }

            // count entropy based on attribute A
            double entropy = 0.0;
            for(String attributeValue: attributeToLabel.keySet()) {
                HashMap<String, Integer> localLabelFrequencyTable = attributeToLabel.get(attributeValue);
                double localInformation = 0.0;
                double localTotalData = 0.0;
                for(String item: localLabelFrequencyTable.keySet()) {
                    localTotalData += localLabelFrequencyTable.get(item);
                }
                for(String item: localLabelFrequencyTable.keySet()) {
                    localInformation -= (localLabelFrequencyTable.get(item)) / (localTotalData) * (Math.log((localLabelFrequencyTable.get(item)) / (localTotalData)) / Math.log(2.0d));
                }
                localInformation *= (localTotalData)/(totaldata);
                entropy += localInformation;
            }

            // count information gain
            double localGain = currentInformation - entropy;
            if(localGain > gain) {
                gain = localGain;
                bestAttribute = attribute;
            }

        }
        return bestAttribute;
    }
}
