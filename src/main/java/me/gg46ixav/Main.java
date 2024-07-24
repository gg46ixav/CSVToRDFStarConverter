package me.gg46ixav;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.system.RDFStar;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.RDF;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
        public static void main(String[] args) {

            /*
            if (args.length < 2) {
                System.out.println("Usage: java CSVtoRDF <csvFile> <identifierColumn>");
                return;
            }
            */
            args = new String[10];
            args[0] = "Vertice";
            args[1] = "C:/Users/theo/Desktop/Data_sf0.01/Person.csv";
            args[2] = "Person";
            args[3] = "String";
            args[4] = "Boolean";
            args[5] = "DateTime";
            args[6] = "String";
            args[7] = "Date";
            args[8] = "String";
            args[9] = "String";


            /*
            String[] splitted = args[0].split("/");
            String fileName = splitted[splitted.length-1];
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
             */

            String name = args[2];
            String[] datatypes = Arrays.copyOfRange(args, 3 ,args.length);

            String type = args[0];
            if(!type.equalsIgnoreCase("Edge") && !type.equalsIgnoreCase("Vertice")){
                System.out.println("Usage: java CSVtoRDF <csvFile> <identifierColumn>");
                return;
            }
            String csvFile = args[1];

            if(type.equalsIgnoreCase("Vertice")) convertVertice(name, csvFile, datatypes);
            else convertEdge(name, csvFile, datatypes);
        }

    private static void convertEdge(String name, String csvFile, String[] datatypes) {
        int identifierColumn1 = 0;
        int identifierColumn2 = 1;

        String line;

        Model model = ModelFactory.createDefaultModel();


        long id = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String[] header = parseCSVLine(br.readLine());

            StringBuilder stringBuilder = new StringBuilder();

            while ((line = br.readLine()) != null) {
                id++;
                String[] data = parseCSVLine(line);

                Triple triple = Triple.ANY;
                String subject = "";
                String tripleString = "";

                for (int i = 0; i < data.length; i++) {

                    if (i == identifierColumn1) {
                        String identifier = data[i];
                        subject = STR."http://example.org/\{identifier}";

                    }else if (i == identifierColumn2) {

                        String identifier = data[i];

                        Resource ex1 = model.createResource(STR."\{subject}");
                        Resource ex2 = model.createResource(STR."http://example.org/\{name+id}");
                        Resource ex3 = model.createResource(STR."http://example.org/\{identifier}");

                        tripleString = STR."<< <\{subject}> <http://example.org/\{name}\{id}> <http://example.org/\{identifier}> >>";


                        String aPropertyString = STR."<http://example.org/\{name+id}> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://example.org/Property> . ";

                        String nameString = STR."<http://example.org/\{name}\{id}> <http://example.org/name> \"\{name}\". ";

                        stringBuilder.append(aPropertyString);
                        stringBuilder.append(nameString);

                        //triple = Triple.create(ex1.asNode(), ex2.asNode(), ex3.asNode());
/*
                        Statement statement = model.createStatement(
                                model.createResource(STR."http://example.org/\{name+id}"),
                                RDF.type,
                                model.createResource("http://example.org/Property"));
                        model.add(statement);
                        Statement statement2 = model.createStatement(
                                model.createResource(STR."http://example.org/\{name+id}"),
                                model.createProperty("http://example.org/name"),
                                name);

                        model.add(statement2);

 */
                    }else {
                        String propertyName = header[i];
                        String propertyValue = data[i];

                        if (!propertyValue.isEmpty()) {
                            String literal = null;

                            if(datatypes.length>=i-1){
                                switch(datatypes[i-2]){
                                    case "DateTime":
                                        literal = STR."\"\{propertyValue}\"^^<\{XSDDatatype.XSDdateTime.getURI()}>";
                                        break;
                                    case "Date":
                                        literal = STR."\"\{propertyValue}\"^^<\{XSDDatatype.XSDdate.getURI()}>";
                                        break;
                                    case "Boolean":
                                        literal = STR."\"\{propertyValue}\"^^<\{XSDDatatype.XSDboolean.getURI()}>";
                                        break;
                                    case "Long":
                                        literal = STR."\"\{propertyValue}\"^^<\{XSDDatatype.XSDlong.getURI()}>";
                                        break;
                                    case "Float":
                                        literal = STR."\"\{propertyValue}\"^^<\{XSDDatatype.XSDfloat.getURI()}>";
                                        break;
                                    case "CastMillisToDateTime":
                                        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                        Date d = new Date();
                                        d.setTime(Long.parseLong(propertyValue));
                                        literal = STR."\"\{DATE_FORMAT.format(d)}\"^^<\{XSDDatatype.XSDdateTime.getURI()}>";
                                        break;
                                    default:
                                        literal = STR."\"\{propertyValue}\"";
                                        break;

                                }
                            }else{
                                literal = STR."\"\{propertyValue}\"";
                            }

                            String property = STR."<http://example.org/\{propertyName}>";

                            String s = STR."\{tripleString} \{property} \{literal} . ";

                            stringBuilder.append(s);
                            /*
                            Statement statement = model.createStatement(model.createResource(
                                            STR."\{triple.toString()}"),
                                            property,
                                            literal);
                            model.add(statement);

                             */
                        }
                    }
                }
            }


            // Print RDF triples
            URL datasetURL = new URL("http://localhost:3030/Finbench_sf_0.01");
            HttpURLConnection connection = (HttpURLConnection) datasetURL.openConnection();
            String encoded = Base64.getEncoder().encodeToString(("admin:Dnx1sayu3vu957x").getBytes(StandardCharsets.UTF_8));
            connection.setRequestProperty("Authorization", "Basic " + encoded);
            connection.setRequestProperty("Content-Type", "text/Turtle");

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            try(OutputStream os = connection.getOutputStream()) {
                byte[] input = stringBuilder.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            //model.write(System.out, "NTRIPLES");
            //RDFDataMgr.write(System.out, model, Lang.TURTLE);
            connection.getInputStream();




            /*
            Query query = QueryFactory.create(queryString);
            try(QueryExecution queryExecution = QueryExecutionFactory.create(query, model)){
                ResultSet resultSet = queryExecution.execSelect();

                for(; resultSet.hasNext();){
                    QuerySolution solution = resultSet.nextSolution();
                    System.out.println(solution.toString());
                }
            }

             */


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void convertVertice(String name, String csvFile, String[] datatypes) {

        int identifierColumn = 0;
        String line;

        Model model = ModelFactory.createDefaultModel();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String[] header = parseCSVLine(br.readLine());

            while ((line = br.readLine()) != null) {
                String[] data = parseCSVLine(line);

                Resource resource = null;

                for (int i = 0; i < data.length; i++) {
                    if (i == identifierColumn) {
                        String identifier = data[i];
                        resource = model.createResource(STR."http://example.org/\{identifier}");
                        resource.addProperty(RDF.type, model.createResource(STR."http://example.org/\{name}"));
                    } else {
                        String propertyName = header[i];
                        String propertyValue = data[i];
                        if (resource != null && !propertyValue.isEmpty()) {
                            Property property = model.createProperty(STR."http://example.org/\{propertyName}");
                            if(datatypes.length>=i+1){
                                switch(datatypes[i-1]){
                                    case "DateTime":
                                        resource.addProperty(property, propertyValue, XSDDatatype.XSDdateTime);
                                        break;
                                    case "Date":
                                        resource.addProperty(property, propertyValue, XSDDatatype.XSDdate);
                                        break;
                                    case "Boolean":
                                        resource.addProperty(property, propertyValue, XSDDatatype.XSDboolean);
                                        break;
                                    case "Long":
                                        resource.addProperty(property, propertyValue, XSDDatatype.XSDlong);
                                        break;
                                    case "Float":
                                        resource.addProperty(property, propertyValue, XSDDatatype.XSDfloat);
                                        break;
                                    case "CastMillisToDateTime":
                                        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                        Date d = new Date();
                                        d.setTime(Long.parseLong(propertyValue));
                                        resource.addProperty(property, DATE_FORMAT.format(d), XSDDatatype.XSDdateTime);
                                        break;
                                    default:
                                        resource.addProperty(property, propertyValue);
                                        break;

                                }
                            }else{
                                resource.addProperty(property, propertyValue);
                            }
                        }
                    }
                }
            }

            // Print RDF triples
            URL datasetURL = new URL("http://localhost:3030/Finbench_sf_0.01");

            HttpURLConnection connection = (HttpURLConnection) datasetURL.openConnection();
            String encoded = Base64.getEncoder().encodeToString(("admin:Dnx1sayu3vu957x").getBytes(StandardCharsets.UTF_8));
            connection.setRequestProperty("Authorization", "Basic " + encoded);
            connection.setRequestProperty("Content-Type", "text/Turtle");

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            OutputStream out = connection.getOutputStream();
            RDFDataMgr.write(out, RDFStar.encodeAsRDF(model.getGraph()), Lang.TRIG);
            connection.getInputStream();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String[] parseCSVLine(String line) {
        String[] tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = tokens[i].replaceAll("^\"|\"$", "");
        }
        return tokens;
    }
}