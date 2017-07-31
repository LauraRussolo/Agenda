package br.com.alura.agenda.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;

import br.com.alura.agenda.modelo.Aluno;

/**
 * Created by Parafuso Solto on 20/02/2017.
 */

public class AlunoDAO extends SQLiteOpenHelper {
    public AlunoDAO(Context context) {
        super(context, "Agenda", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) { //metodo chamado ao criar o bd
        //System.out.println("entrou no create");
        Log.d(null,"entrou no create");
        String sql = "CREATE TABLE Alunos (id INTEGER PRIMARY KEY, " +
                "nome TEXT NOT NULL, " +
                "endereco TEXT, " +
                "telefone TEXT, " +
                "site TEXT, " +
                "nota REAL, " +
                "caminhoFoto TEXT);";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { //metodo chamado ao atualizar o bd
        //System.out.println("entrou no upgrade");
        Log.d(null,"entrou no upgrade");
        String sql; //string declarada apenas uma vez e seu valor muda conforme necessário
        switch (oldVersion) {
            case 1:
                sql = "ALTER TABLE Alunos ADD COLUMN caminhoFoto TEXT;";
                db.execSQL(sql);
                //switch case sem break: ao entrar no primeiro case (seja ele o primeiro, segundo ou milhonésimo) o código abaixo de todos os case x: é executado, como se ignorasse a linha do case x:
        }
    }

    public List<Aluno> buscaAlunos() {
        String sql = "SELECT * FROM Alunos;"; //query select
        SQLiteDatabase db = getReadableDatabase(); //solicitando bd para leitura
        Cursor c = db.rawQuery(sql, null); //cursor em posição vazia

        List<Aluno> alunos = new ArrayList<Aluno>(); //instancia arraylist de alunos

        while(c.moveToNext()) {
            Aluno aluno = new Aluno(); //isntancia novo aluno
            aluno.setId(c.getLong(c.getColumnIndex("id"))); //popula aluno com dados q está buscando
            aluno.setNome(c.getString(c.getColumnIndex("nome")));
            aluno.setTelefone(c.getString(c.getColumnIndex("telefone")));
            aluno.setEndereco(c.getString(c.getColumnIndex("endereco")));
            aluno.setSite(c.getString(c.getColumnIndex("site")));
            aluno.setNota(c.getDouble(c.getColumnIndex("nota")));
            aluno.setCaminhoFoto(c.getString(c.getColumnIndex("caminhoFoto")));
            alunos.add(aluno); //insere aluno populado na lista de alunos
        }
        c.close(); //a memoria com o resultado da query é liberada, senão o android não libera a memória

        return alunos;
    }

    public void insere(Aluno aluno) {
        SQLiteDatabase db = getWritableDatabase(); //pede o db para o sqlite

        ContentValues dados = pegaDadosDoAluno(aluno);

        System.out.println("dados:" + dados);

        db.insert("Alunos", null, dados);
    }

    public void deleta(Aluno aluno) {
        SQLiteDatabase db = getWritableDatabase();
        String[] params = {aluno.getId().toString()};
        db.delete("Alunos", "id = ?", params);
    }

    public void altera(Aluno aluno) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues dados = pegaDadosDoAluno(aluno);
        String[] params = {aluno.getId().toString()};
        db.update("Alunos", dados, "id = ?", params);
    }

    @NonNull
    private ContentValues pegaDadosDoAluno(Aluno aluno) {
        ContentValues dados = new ContentValues();
        dados.put("nome", aluno.getNome()); //esse método put() impede sql injection automaticamente, diferente do que seria uma instrução de inclusão pegando os dados digitados nos campos
        dados.put("endereco", aluno.getEndereco());
        dados.put("telefone", aluno.getTelefone());
        dados.put("site", aluno.getSite());
        dados.put("nota", aluno.getNota());
        dados.put("caminhoFoto", aluno.getCaminhoFoto());
        return dados;
    }

    public boolean alunoExiste(String telefone) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM Alunos WHERE telefone = ?", new String[]{telefone});
        int i = c.getCount();
        c.close();

        return i>0;
    }
}
