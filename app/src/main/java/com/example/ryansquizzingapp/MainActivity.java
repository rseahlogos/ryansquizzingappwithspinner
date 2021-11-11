package com.example.ryansquizzingapp;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    int score1 = 0;
    String answer;
    String[] questions;
    String[][] answers;
    Button buttons[] = {null, null, null, null};
    TextView text1;
    TextView score;

    int guesses[] = {0};
    boolean flag1= false;
    Button change;

    URL url;
    InputStream is = null;
    BufferedReader br;
    String line;
    ArrayList<String> quizzes1;
    ArrayList<String> quizzes2;
    ArrayList<String> quizNames;

    Set<String> quizLinks;
    String changequiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        new Content().execute();


        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.quizzes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


        change = (Button) findViewById(R.id.change);


        change.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                run(changequiz);
            }
        });

    }
    private class Content extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Document doc = null;
            try {
                doc = Jsoup.connect("https://sites.google.com/asianhope.org/mobileresources/home").get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Elements links =doc.select("a[href]");


            quizLinks = new LinkedHashSet<>();
           // quizLinks = new ArrayList<String>();
            quizNames = new ArrayList<String>();

            for(Element link:links)
            {

                if(link.attr("href").contains("mobileresources/q"))
                {

                    quizLinks.add("https://sites.google.com"+link.attr("href"));

                    quizNames.add(link.text());
                }

            }

            System.out.println(quizLinks.size()+" quizzes found");
            quizzes1 = new ArrayList<String>();
            for(String url:quizLinks)
            {
                System.out.println("connecting to "+url);
                try {
                    doc = Jsoup.connect(url).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //
                boolean strictMode = true;
                String paragraphTagOpen = "<p[^>]+>";
                String paragraphTagClose = "</p[^>]*>";
                String quizTagOpen = "<quiz";
                String quizTagClose ="</quiz>";



                String quiz = doc.html();
                quiz = Parser.unescapeEntities(quiz, strictMode);
                int beginQuizXml = quiz.lastIndexOf(quizTagOpen);
                int endQuizXml = quiz.lastIndexOf(quizTagClose) + quizTagClose.length();

                Validate.isTrue(beginQuizXml>=0&&endQuizXml>=0," quiz not found ");

                quiz = quiz.substring(beginQuizXml, endQuizXml).replaceAll(paragraphTagOpen, "")
                        .replaceAll(paragraphTagClose, "").trim();
                //
                quizzes1.add(quiz);


            }

            //run("q1");

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            quizzes2=quizzes1;




        }
    }
    private void run(String quizname) {
        try {



            String deez= quizzes2.get(quizNames.indexOf(quizname));
            InputStream is = new ByteArrayInputStream(deez.getBytes(Charset.forName("UTF-8")));

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = dBuilder.parse(is);
            org.w3c.dom.Element element = doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("question");
            questions = new String[nList.getLength()];
            answers = new String[nList.getLength()][5];
            guesses[0] = 0;
            score.setText("Score: TBD");


            for (int l = 0; l < buttons.length; l++) {
                int j = l;
                buttons[j].setEnabled(true);
            }
//IS THIS ON GITHUB

            for (int p = 0; p < nList.getLength(); p++) {

                Node node = nList.item(p);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    org.w3c.dom.Element element2 = (org.w3c.dom.Element) node;
                    questions[p] = getValue("stem", element2);
                    answers[p][0] = getValue("answerA", element2);
                    answers[p][1] = getValue("answerB", element2);
                    answers[p][2] = getValue("answerC", element2);
                    answers[p][3] = getValue("answerD", element2);
                    answers[p][4] = getValue("key", element2);
                    if (!(answers[p][4].equals(answers[p][3]) || answers[p][4].equals(answers[p][2]) || answers[p][4].equals(answers[p][1]) ||
                            answers[p][4].equals(answers[p][0]))) {
                        throw new Exception("There is no correct answer"); //this doesn't work D:
                    }

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        buttons[0] = (Button) findViewById(R.id.button1);
        buttons[1] = (Button) findViewById(R.id.button2);
        buttons[2] = (Button) findViewById(R.id.button3);
        buttons[3] = (Button) findViewById(R.id.button4);
        text1 = (TextView) findViewById(R.id.text1);
        score = (TextView) findViewById(R.id.score);
        final int[] i = {0};
        next(i[0]);


        //CODE THAT IM TOO LAZY TO FIND A FIX FOR
        buttons[0].setBackgroundColor(Color.CYAN);
        buttons[1].setBackgroundColor(Color.CYAN);
        buttons[2].setBackgroundColor(Color.CYAN);
        buttons[3].setBackgroundColor(Color.CYAN);
        //IGNORE THIS


        for (int a = 0; a < 4; a++) {
            int k = a;
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
                    } else {
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


    private String getValue(String tag, org.w3c.dom.Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }

    private void next(int i) {
        if (i == questions.length) {
            text1.setText("You are done! Your score should be displayed above ^");
            double fscore = (double) questions.length / (double) guesses[0] * 100.00;
            String fscoreString = String.format("Score: %.2f%%", fscore);
            score.setText(fscoreString);
            for (int l = 0; l < buttons.length; l++) {
                int j = l;
                buttons[j].setEnabled(false);
            }
        } else {
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
        changequiz=text;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private static String extractQuiz(String html) throws IOException {
        boolean strictMode = true;
        String paragraphTagOpen = "<p[^>]+>";
        String paragraphTagClose = "</p[^>]*>";
        String quizTagOpen = "<quiz";
        String quizTagClose = "</quiz>";


        String quiz = html;
        quiz = Parser.unescapeEntities(quiz, strictMode);
        int beginQuizXml = quiz.lastIndexOf(quizTagOpen);
        int endQuizXml = quiz.lastIndexOf(quizTagClose) + quizTagClose.length();

        Validate.isTrue(beginQuizXml >= 0 && endQuizXml >= 0, " quiz not found ");

        quiz = quiz.substring(beginQuizXml, endQuizXml).replaceAll(paragraphTagOpen, "")
                .replaceAll(paragraphTagClose, "").trim();
        return quiz;
    }

}
