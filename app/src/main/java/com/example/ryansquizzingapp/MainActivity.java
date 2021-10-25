package com.example.ryansquizzingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    int score1=0;
    String answer;
    String[] questions;
    String[][] answers;
    Button buttons[] = {null, null, null, null};
    TextView text1, score, html;
    int guesses[] = {0};

    URL url;
    InputStream is = null;
    BufferedReader br;
    String line;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.quizzes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        run("ryanquiz1.xml");
        html = (TextView) findViewById(R.id.html);
        /*String htmlcode="";
        try {
            url = new URL("https://sites.google.com/asianhope.org/mobileresources");
            is = url.openStream();  // throws an IOException
            br = new BufferedReader(new InputStreamReader(is));

            while ((line = br.readLine()) != null) {
                //System.out.println(line);
                htmlcode=htmlcode.concat(line);
            }
        } catch (
                MalformedURLException mue) {
            mue.printStackTrace();
        } catch (
                IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ioe) {
                // nothing to see here
            }
        }
        html.setText(htmlcode);
        */
        Ion.with(getApplicationContext()).load("https://sites.google.com/asianhope.org/mobileresources").asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {

                html.setText(result);
            }
        });


    }
    private void run (String quizname) {
        try {

            InputStream is = getAssets().open(quizname);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);
            Element element=doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("question");
            questions = new String[nList.getLength()];
            answers = new String[nList.getLength()][5];
            guesses[0]=0;
            score.setText("Score: TBD");
            for (int l=0; l<buttons.length; l++){
                int j = l;
                buttons[j].setEnabled(true);
            }
//IS THIS ON GITHUB

            for (int p=0; p<nList.getLength(); p++) {

                Node node = nList.item(p);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;
                    questions[p]=getValue("stem", element2);
                    answers[p][0]=getValue("answerA", element2);
                    answers[p][1]=getValue("answerB", element2);
                    answers[p][2]=getValue("answerC", element2);
                    answers[p][3]=getValue("answerD", element2);
                    answers[p][4]=getValue("key", element2);
                    if(!(answers[p][4].equals(answers[p][3]) || answers[p][4].equals(answers[p][2]) ||answers[p][4].equals(answers[p][1]) ||
                            answers[p][4].equals(answers[p][0]))) {
                        throw new Exception("There is no correct answer"); //this doesn't work D:
                    }

                }

            }

        } catch (Exception e) {e.printStackTrace();}


        buttons[0] = (Button) findViewById(R.id.button1);
        buttons[1] = (Button) findViewById(R.id.button2);
        buttons[2] = (Button) findViewById(R.id.button3);
        buttons[3] = (Button) findViewById(R.id.button4);
        text1 = (TextView) findViewById(R.id.text1);
        score = (TextView) findViewById(R.id.score);
        final int[] i = {0};
        next(i[0]);
        // final int[] guesses={0};

        //CODE THAT IM TOO LAZY TO FIND A FIX FOR
        buttons[0].setBackgroundColor(Color.CYAN);
        buttons[1].setBackgroundColor(Color.CYAN);
        buttons[2].setBackgroundColor(Color.CYAN);
        buttons[3].setBackgroundColor(Color.CYAN);
        //IGNORE THIS




        for(int a=0; a<4; a++) {
            int k=a;
            buttons[k].setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (buttons[k].getText().equals(answers[i[0]][4])) {
                        text1.setText("Correct Answer!");
                        for (int b = 0; b < 4; b++) {
                            int j = b;
                            buttons[j].setClickable(false);
                            buttons[j].postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    buttons[j].setClickable(true);
                                }
                            }, 500);
                        }
                        guesses[0]++;
                        i[0]++;
                        next(i[0]);
                    }
                    else {
                        text1.setText("Try Again!");
                        for (int b = 0; b < 4; b++) {
                            int j = b;
                            buttons[j].setClickable(false);
                            buttons[j].setBackgroundColor(Color.RED);
                            buttons[j].postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    buttons[j].setClickable(true);
                                    buttons[j].setBackgroundColor(Color.CYAN);
                                    text1.setText(questions[i[0]]);
                                }
                            }, 500);
                        }

                        guesses[0]++;
                    }
                }
            });
        }
    }

    private String getValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }
    private void next(int i) {
        if(i==questions.length){
            text1.setText("You are done! Your score should be displayed above ^");
            double fscore = (double)questions.length/(double)guesses[0] * 100.00;
            String fscoreString = String.format("Score: %.2f%%", fscore);
            score.setText(fscoreString);
            for (int l=0; l<buttons.length; l++){
                int j = l;
                buttons[j].setEnabled(false);
            }
        }
        else {
            text1.setText(questions[i]);
            buttons[0].setText(answers[i][0]);
            buttons[1].setText(answers[i][1]);
            buttons[2].setText(answers[i][2]);
            buttons[3].setText(answers[i][3]);
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String text = adapterView.getItemAtPosition(i).toString();
        run(text);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    }


//emergency code incase im bad at life
/*
    String[] questionsold = {
            "What is 9 + 10?",
            "What is 1+1?",
            "What is 2+3?",
            "What is 9x9?",
            "What is 9+9?",
            "What is 9/9",
            "What is sqrt9?",
            "Who is Mr. Skogens favorite student?",
            "Who is Mr Skogens favorite Logos teacher?",
            "Who is going to get a 100% on this assignment?"
    };
    String[][] answersold = {
            {"21", "19", "20", "910", "19"}, //SET ANSWER TO 19
            {"1", "2", "11", "0", "2"},
            {"5", "23", "2+3-1+1-2", "-5", "5"},
            {"18", "81.1", "99", "81", "81"},
            {"18", "81.1", "99", "81", "18"},
            {"undefined", "0", "1", "9", "1"},
            {"3", "-3.1", "81", "1", "3"},
            {"Bryant", "Ryan", "Yejin", "Andy", "Ryan"},
            {"Ms. Mendoza", "Mr. Seah", "Mr. Skogen", "Mr. Barrett", "Ms. Mendoza"},
            {"Bryant", "Ryan", "Yejin", "Andy", "Ryan"}
    };


 */
    /*
String bruh= String.format("%s", guesses[0]);
                    score.setText(bruh);
*/

/* buttons[1].setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(buttons[1].getText().equals(answers[i[0]][4])){
                    text1.setText("Correct Answer!");
                    for(int b=0; b<4; b++) {
                        int j=b;
                        buttons[j].setClickable(false);
                        buttons[j].postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                buttons[j].setClickable(true);
                                guesses[0]++;
                                i[0]++;
                                next(i[0]);
                            }
                        }, 1000);
                    }

                }
                else{
                    text1.setText("Try Again!");
                    for(int b=0; b<4; b++) {
                        int j=b;
                        buttons[j].setClickable(false);
                        buttons[j].setBackgroundColor(Color.RED);
                        buttons[j].postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                buttons[j].setClickable(true);
                                text1.setText(questions[j]);
                                buttons[j].setBackgroundColor(Color.CYAN);
                                guesses[0]++;
                            }
                        }, 1000);
                    }
                }
            }
        });

        buttons[2].setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(buttons[2].getText().equals(answers[i[0]][4])){
                    text1.setText("Correct Answer!");
                    for(int b=0; b<4; b++) {
                        int j=b;
                        buttons[j].setClickable(false);
                        buttons[j].postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                buttons[j].setClickable(true);
                                guesses[0]++;
                                i[0]++;
                                next(i[0]);
                            }
                        }, 1000);
                    }

                }
                else{
                    text1.setText("Try Again!");
                    for(int b=0; b<4; b++) {
                        int j=b;
                        buttons[j].setClickable(false);
                        buttons[j].setBackgroundColor(Color.RED);
                        buttons[j].postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                buttons[j].setClickable(true);
                                text1.setText(questions[j]);
                                buttons[j].setBackgroundColor(Color.CYAN);
                                guesses[0]++;
                            }
                        }, 1000);
                    }
                }
            }
        });
        buttons[3].setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(buttons[3].getText().equals(answers[i[0]][4])){
                    text1.setText("Correct Answer!");
                    for(int b=0; b<4; b++) {
                        int j=b;
                        buttons[j].setClickable(false);
                        buttons[j].postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                buttons[j].setClickable(true);
                                guesses[0]++;
                                i[0]++;
                                next(i[0]);
                            }
                        }, 1000);
                    }

                }
                else{
                    text1.setText("Try Again!");
                    for(int b=0; b<4; b++) {
                        int j=b;
                        buttons[j].setClickable(false);
                        buttons[j].setBackgroundColor(Color.RED);
                        buttons[j].postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                buttons[j].setClickable(true);
                                text1.setText(questions[j]);
                                buttons[j].setBackgroundColor(Color.CYAN);
                                guesses[0]++;
                            }
                        }, 1000);
                    }
                }
            }
        }); */