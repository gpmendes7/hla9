package br.com.covid.app;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

public class LerArquivoCSV {
	
	public static void main(String[] args) throws IOException {
		
		Reader reader = Files.newBufferedReader(Paths.get("src/main/resources/csv/TB_parearNx.csv"));
		CSVReader csvReader = new CSVReaderBuilder(reader).build();
		
		List<String[]> registros = csvReader.readAll();
		
		for(String[] registro: registros) {	
			System.out.println("id_paciente: " + registro[0]);
			System.out.println("idade: " + registro[1]);
			System.out.println("FxEtaria: " + registro[2]);
			System.out.println("cidade: " + registro[3]);
		}
	}

}
