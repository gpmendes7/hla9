package br.com.covid.app;

import static br.com.covid.util.StringUtil.normalizarString;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

public class Pareamento {
	
	private final static int TAMANHO_PAREAMENTO = 5;
	private final static int INDICE_RACA = 5;
	private final static int INDICE_SEXO = 4;
	private final static int INDICE_REGIAO = 9;
	private final static int INDICE_FAIXA_ETARIA = 2;
	private final static int INDICE_HLA = 8;
	private final static int INDICE_OBSERVACAO_USO = 12;
	
	
	private static List<String[]> casos;
	private static List<String[]> controles;
	private static List<String[]> pareamento;
	private static List<String[]> controlesFiltrados;
	
	public static void main(String[] args) throws IOException, ParseException {
        casos = carregarRegistros("src/main/resources/csv/casos.csv");
		
		casos = casos.stream()
		             .skip(1)
		             .collect(Collectors.toList());
		
		System.out.println("casos.size(): " + casos.size());
		
        controles = carregarRegistros("src/main/resources/csv/controles.csv");
		
        controles.stream()
     			 .skip(1)
                 .collect(Collectors.toList());
        
		System.out.println("controles.size(): " + controles.size());
		
		pareamento = new ArrayList<String[]>();
		
		pareamento.addAll(casos);
		
		for (String[] caso : casos) {	
			controlesFiltrados = filtrarControlesNaoUsados(controles);
			System.out.println("Número de controles filtrados não usados: " + controlesFiltrados.size());
						
			filtrarPorRaca(caso);
			
			filtrarPorSexo(caso);
			
			ordenarPorRegiao();
			
			ordenarPorFaixaEtaria();
			
			ordenarPorHLA();
					
			controlesFiltrados = marcarRegistros(controlesFiltrados);
			System.out.println("Número de controles marcados como usados: " + controlesFiltrados.size());
					
			for(String[] controleFiltrado: controlesFiltrados) {
				String[] registroControleFiltrado = {controleFiltrado[0], controleFiltrado[1], controleFiltrado[2], controleFiltrado[3], 
						controleFiltrado[4], controleFiltrado[5], controleFiltrado[6], controleFiltrado[7],
						controleFiltrado[8], controleFiltrado[9], controleFiltrado[10], controleFiltrado[11]};
				pareamento.add(registroControleFiltrado);
			}
			
			System.out.println("****************************");
		}
		
		System.out.println("Número de registros pareados: " + pareamento.size());
		salvarRegistros(pareamento);
	}

	private static void filtrarPorRaca(String[] caso) {
		System.out.println("Raça: " + caso[INDICE_RACA]);
		
		List<String[]> controlesFiltradosAux1 = new ArrayList<String[]>();
		List<String[]> controlesFiltradosAux2 = new ArrayList<String[]>();
		int filtragem = 0;
		
		while(filtragem < 3) { 
			if(caso[INDICE_RACA].equals("PRETA")) {
				if(filtragem == 0) {
					controlesFiltradosAux1 = filtrarPorCampo(controlesFiltrados, "PRETA", INDICE_RACA);
					System.out.println("Número de controles filtrados por raça PRETA: " + controlesFiltradosAux1.size());
				}
				else if(filtragem == 1) {
					controlesFiltradosAux1 = filtrarPorCampo(controlesFiltrados, "PARDA", INDICE_RACA);
					System.out.println("Número de controles filtrados por raça PARDA: " + controlesFiltradosAux1.size());
				}
			}
			else if(caso[INDICE_RACA].equals("BRANCA")) {
				if(filtragem == 0) {
					controlesFiltradosAux1 = filtrarPorCampo(controlesFiltrados, "BRANCA", INDICE_RACA);
					System.out.println("Número de controles filtrados por raça BRANCA: " + controlesFiltradosAux1.size());
				}
				else if(filtragem == 1) {
					controlesFiltradosAux1 = filtrarPorCampo(controlesFiltrados, "PARDA", INDICE_RACA);
					System.out.println("Número de controles filtrados por raça PARDA: " + controlesFiltradosAux1.size());
				}
			}
			else if(caso[INDICE_RACA].equals("PARDA")) {
				if(filtragem == 0) {
					controlesFiltradosAux1 = filtrarPorCampo(controlesFiltrados, "PARDA", INDICE_RACA);
					System.out.println("Número de controles filtrados por raça PARDA: " + controlesFiltradosAux1.size());
				}
				else if(filtragem == 1) {
					controlesFiltradosAux1 = filtrarPorCampo(controlesFiltrados, "PRETA", INDICE_RACA);
					System.out.println("Número de controles filtrados por raça PRETA: " + controlesFiltradosAux1.size());
				}
				else if(filtragem == 2) {
					controlesFiltradosAux1 = filtrarPorCampo(controlesFiltrados, "BRANCA", INDICE_RACA);
					System.out.println("Número de controles filtrados por raça BRANCA: " + controlesFiltradosAux1.size());
				}
			}
			
			controlesFiltradosAux2.addAll(controlesFiltradosAux1);
			
			if(controlesFiltradosAux2.size() >= TAMANHO_PAREAMENTO) {
				controlesFiltrados = controlesFiltradosAux2;
				break;
			} 
			
			filtragem++; 
		}
	}
	
	private static void filtrarPorSexo(String[] caso) {
		System.out.println("Sexo: " + caso[INDICE_SEXO]);
		
		List<String[]> controlesFiltradosAux =  filtrarPorCampo(controlesFiltrados, caso[INDICE_SEXO], INDICE_SEXO);
		
		if(controlesFiltradosAux.size() >= TAMANHO_PAREAMENTO) {
			controlesFiltrados = controlesFiltradosAux;
			System.out.println("Número de controles filtrados por campo sexo: " + controlesFiltrados.size());
		}
	}
	
	private static void ordenarPorRegiao() {
		/*
		System.out.println("Controles filtrados antes de ordenar por região: ");
		for (String[] controlesFiltrado : controlesFiltrados) {
			System.out.println(controlesFiltrado[INDICE_REGIAO]);
		}
		*/
		
		Collections.sort(controlesFiltrados, (a, b) -> 
						Integer.valueOf((a[INDICE_REGIAO])).compareTo(Integer.valueOf((b[INDICE_REGIAO]))));
		
		/*
		System.out.println("Controles filtrados após ordenar por região: ");
		for (String[] controlesFiltrado : controlesFiltrados) {
			System.out.println(controlesFiltrado[INDICE_REGIAO]);
		}
		*/
	}
	
	private static void ordenarPorFaixaEtaria() {
		/*
		System.out.println("Controles filtrados antes de ordenar por faixa etária: ");
		for (String[] controlesFiltrado : controlesFiltrados) {
			System.out.println(controlesFiltrado[INDICE_FAIXA_ETARIA]);
		}
	    */
		
		Collections.sort(controlesFiltrados, (a, b) -> 
						Integer.valueOf((a[INDICE_FAIXA_ETARIA])).compareTo(Integer.valueOf((b[INDICE_FAIXA_ETARIA]))));
		
		Collections.reverse(controlesFiltrados);
		
		/*
		System.out.println("Controles filtrados após ordenar por faixa etária: ");
		for (String[] controlesFiltrado : controlesFiltrados) {
			System.out.println(controlesFiltrado[INDICE_FAIXA_ETARIA]);
		}
		*/
	}
	
	private static void ordenarPorHLA() {
		/*
		System.out.println("Controles filtrados antes de ordenar por HLA: ");
		for (String[] controlesFiltrado : controlesFiltrados) {
			System.out.println(controlesFiltrado[INDICE_HLA]);
		}
		*/
		
		Collections.sort(controlesFiltrados, (a, b) -> 
						Integer.valueOf((a[INDICE_HLA])).compareTo(Integer.valueOf((b[INDICE_HLA]))));
		
		/*
		System.out.println("Controles filtrados após ordenar por HLA: ");
		for (String[] controlesFiltrado : controlesFiltrados) {
			System.out.println(controlesFiltrado[INDICE_HLA]);
		}
		*/
	}
	
	private static List<String[]> carregarRegistros(String nomeArquivo) throws IOException {
		Reader reader = Files.newBufferedReader(Paths.get(nomeArquivo));
		CSVReader csvReader = new CSVReaderBuilder(reader).build();
		
		return csvReader.readAll();
	}
	
	private static List<String[]> filtrarControlesNaoUsados(List<String[]> registros) {
		return registros.stream()
				.filter(r -> r[INDICE_OBSERVACAO_USO] == null || r[INDICE_OBSERVACAO_USO].equals(""))
				.collect(Collectors.toList());
	}
	
	private static List<String[]> filtrarPorCampo(List<String[]> registros, String campo, int indiceCampo) {
		return registros.stream()
				        .filter(r -> normalizarString(r[indiceCampo]).equals(normalizarString(campo)))
				        .collect(Collectors.toList());
	}
	
	private static List<String[]> marcarRegistros(List<String[]> registros) {

		registros.stream()
		         .limit(TAMANHO_PAREAMENTO)
		         .forEach(r -> r[INDICE_OBSERVACAO_USO] = "usado");
		
		List<String[]> registrosUsados = registros.stream()
			                                    .filter(r -> r[INDICE_OBSERVACAO_USO] != null 
			                                            && !r[INDICE_OBSERVACAO_USO].equals(""))
			                                    .collect(Collectors.toList());
		
		return registrosUsados;
	}
	
	private static void salvarRegistros(List<String[]> pareamento) throws ParseException, FileNotFoundException, IOException {
		 String[] cabecalho = {"id_paciente", "idade", "FxEtaria", "cidade", "sexo_nascimento", "cor_pele", "grupo",
				               "MESOREGIAO", "OrdHLA", "OrdRegiao", "ale", "MUNICIPIO_IBGE"};
	        
		 pareamento.add(0, cabecalho);
		 		 
		 String nomeArquivo = "src/main/resources/csv/pareamento.csv";

        try (var fos = new FileOutputStream(nomeArquivo);
            var osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            var writer = new CSVWriter(osw)) {
            writer.writeAll(pareamento);
       }
		
	}

}
