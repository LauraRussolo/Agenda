package br.com.alura.agenda;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Parafuso Solto on 17/03/2017.
 */

//Classe que irá tratar de conexões com o servidor (envio de notas e recebimento da média das notas)
public class Webclient {

    public String post(String json){
        try {
            URL url = new URL("https://www.caelum.com.br/mobile"); //url do servidor, que está pronto para receber esse tipo de reqeuisição
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //openConnection() retorna um objeto URLConnection, que repreenta uma conexão URL genérica (pode se rum arquivo, um diretório, outro objeto. Fazemos o Cast porque queremos uma conexão http

            connection.setRequestProperty("Content-type", "application/json"); //incluimos uma propriedade informando que estamos enviando um json
            connection.setRequestProperty("Accept", "application/json"); //incluimos uma propriedade informando que aceitamos como resposta um json

            connection.setDoOutput(true);//informamos que queremos enciar dados ao servidor, ou seja, fazer um post
            connection.setDoInput(true);

            OutputStream out = connection.getOutputStream(); //pedimos um stream de saída
            PrintStream outPut = new PrintStream(out); //criamos um objeto PrintStream com o stream de saída. O stream de saída aceita unicamente que escrevamos arrays de bytes ou inteiros. Um printstream adiciona funcionalidades ao stream de saida, permitindo escrever diversos tipos de dados (strings, booleans, floats...)
            outPut.println(json);//inserimos nosso json ao corpo da requisição

            connection.connect(); //establece a conexão com o nosso servidor

            InputStream inp = connection.getInputStream(); //solicitamos um stream de entrada de dados a nossa conexão
            Scanner scanner = new Scanner(inp); //atribuímos nossa conexão de entrada a um objeto Scanner, que consegue ler a resposta e conveter apra diversos tipos primitivos de dados
            String resposta = scanner.next(); //captura a resposta em formato string

            return resposta;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
