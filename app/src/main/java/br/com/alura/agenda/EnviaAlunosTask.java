package br.com.alura.agenda;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.List;

import br.com.alura.agenda.converter.AlunoConverter;
import br.com.alura.agenda.dao.AlunoDAO;
import br.com.alura.agenda.modelo.Aluno;

/**
 * Created by Parafuso Solto on 18/03/2017.
 */

public class EnviaAlunosTask extends AsyncTask<Void, Void, String> {
    private Context contexto;
    private ProgressDialog dialog;

    public EnviaAlunosTask(Context contexto) {
        this.contexto = contexto;
    }

    @Override
    protected void onPreExecute() {
        dialog  = ProgressDialog.show(contexto, "Aguarde!", "Enviando Alunos", true, true);
    }

    @Override
    protected String doInBackground(Void... objects) {
        AlunoDAO dao = new AlunoDAO(contexto);
        List<Aluno> alunos = dao.buscaAlunos();
        dao.close();

        AlunoConverter conversor = new AlunoConverter();
        String json = conversor.converterParaJson(alunos);

        Webclient client = new Webclient();
        String resposta = client.post(json);

        return resposta; //esse return é para o método onPostExecute
    }

    @Override
    protected void onPostExecute(String resposta) { //esse méetodo executará na thread principal do app
        dialog.dismiss();
        Toast.makeText(contexto, resposta, Toast.LENGTH_LONG).show();
    }
}
