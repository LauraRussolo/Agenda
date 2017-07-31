package br.com.alura.agenda;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import br.com.alura.agenda.dao.AlunoDAO;
import br.com.alura.agenda.modelo.Aluno;

public class FormularioActivity extends AppCompatActivity {

    public static final int CODIGO_CAMERA = 456;
    private FormularioHelper helper;
    private String caminhofoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        helper = new FormularioHelper(this);
        Intent intent = getIntent(); //recupera o intent usada pra abrir essa tela
        Aluno aluno = (Aluno) intent.getSerializableExtra("aluno");
        if(aluno != null){ //se o intent vem do botão de adicionar novo aluno, o Extra virá como null
            helper.preencheFormulario(aluno);
        }

        Button botaoFoto = (Button) findViewById(R.id.formulario_botao_foto);
        botaoFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //informa ao android que queremos usar a câmera
                caminhofoto = getExternalFilesDir(null) + "/" + System.currentTimeMillis() + ".jpg"; //captura o caminho do diretório aonde a aplicação esta instalada e atribui um nome em milisegundos ao arquivos
                File arquivoFoto = new File(caminhofoto); //cria um arquivo com o caminho especificado (na verdade, um objeto do tipo arquivo, não o arquivo propriamente dito)
                intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(arquivoFoto)); //MediaStore.EXTRA_OUTPUT indica ao android que ele deve salvar a imagem no caminho indicado pelo segundo parametro. O arquivo será criado de fato aqui
                startActivityForResult(intentCamera, CODIGO_CAMERA); //starta a activity e só continua depois de obter o retorno (deve sobreescrever o metodo onActivityResult para tratar)
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CODIGO_CAMERA && resultCode == Activity.RESULT_OK) { //todas as activitiesForResult entram nesse método ao encerrarem, devemos tratar o retorno para saber de onde veio. Verifica também se o usário completou a ação de tirar a foto
            helper.carregaImagem(caminhofoto);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_formulario, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_formulario_ok:
                Aluno aluno = helper.pegaAluno();

                AlunoDAO dao = new AlunoDAO(this);
                if(aluno.getId() != null) {
                    dao.altera(aluno);
                } else {
                    dao.insere(aluno);
                }

                dao.close(); //método da classe SQLiteOpenHelper, fecha a conexão com o banco

                Toast.makeText(FormularioActivity.this, "Aluno "+ aluno.getNome() +" salvo!", Toast.LENGTH_SHORT).show();

                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
