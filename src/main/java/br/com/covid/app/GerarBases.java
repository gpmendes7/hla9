package br.com.covid.app;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

public class GerarBases {
	
	public static void main(String[] args) throws IOException {
		Reader reader = Files.newBufferedReader(Paths.get("src/main/resources/csv/TB_parearNx.csv"));
		CSVReader csvReader = new CSVReaderBuilder(reader).build();
		
		List<String[]> casos = new ArrayList<String[]>();
		List<String[]> controles = new ArrayList<String[]>();
		
		List<String[]> registros = csvReader.readAll();
				
		for(String[] registro: registros) {	
			if(registro[6].equals("REDOME")) {
				String[] registroControles = {registro[0], registro[1], registro[2], registro[3], registro[4], registro[5], registro[6],
						registro[7], registro[8], registro[9], registro[10], registro[11], ""};
				controles.add(registroControles);
			} else if(registro[6].equals("TB")) {
				casos.add(registro);
			}
		}
		
		System.out.println("casos: " + casos.size());
		System.out.println("controles: " + controles.size());
		
		casos.add(0, registros.get(0));
		
		String[] cabecalhoControles = {"id_paciente", "idade" , "FxEtaria", "cidade", "sexo_nascimento", "cor_pele", 
				  "grupo", "MESOREGIAO", "OrdHLA", "OrdRegiao", "ale", "MUNICIPIO_IBGE", "observacaoUso"};
		
		controles.add(0, cabecalhoControles);
		
		String arquivoCasos = "src/main/resources/csv/casos.csv";

        try (var fos = new FileOutputStream(arquivoCasos);
             var osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
             var writer = new CSVWriter(osw)) {
             writer.writeAll(casos);
		
	    }
        
        String arquivoControles = "src/main/resources/csv/controles.csv";

        try (var fos = new FileOutputStream(arquivoControles);
             var osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
             var writer = new CSVWriter(osw)) {
             writer.writeAll(controles);
		
	    }
        
	} 

}
