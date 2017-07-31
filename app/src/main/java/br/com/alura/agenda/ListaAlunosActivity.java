package br.com.alura.agenda;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import br.com.alura.agenda.adapter.AlunosAdapter;
import br.com.alura.agenda.converter.AlunoConverter;
import br.com.alura.agenda.dao.AlunoDAO;
import br.com.alura.agenda.modelo.Aluno;

public class ListaAlunosActivity extends AppCompatActivity {

    public static final int CODIGO_TELEFONE = 123;
    public static final int CODIGO_SMS = 456;

    private ListView listaAlunos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lista_alunos);

        //solicita permissão para receber sms
        if (ActivityCompat.checkSelfPermission(ListaAlunosActivity.this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) { //verifica se a permissão para SMS já foi condedida pelo usuario
            ActivityCompat.requestPermissions(ListaAlunosActivity.this, new String[] {Manifest.permission.RECEIVE_SMS}, CODIGO_SMS); //requisita a permissão
        }

        listaAlunos = (ListView) findViewById(R.id.lista_alunos);

        listaAlunos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> lista, View item, int position, long id) {
                Aluno aluno = (Aluno) listaAlunos.getItemAtPosition(position);
                Intent vaiProFormulario = new Intent(ListaAlunosActivity.this, FormularioActivity.class);
                vaiProFormulario.putExtra("aluno", aluno);
                startActivity(vaiProFormulario);
            }
        });

        Button novoAluno = (Button) findViewById(R.id.lista_alunos_novo_aluno);
        novoAluno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent vaiProFormulario = new Intent(ListaAlunosActivity.this, FormularioActivity.class);
                startActivity(vaiProFormulario);
            }
        });

        registerForContextMenu(listaAlunos);
    }

    @Override
    protected void onResume() {
        super.onResume(); //chama o comprtamento padrão do método antes
        carregaLista();
    }

    private void carregaLista() {
        AlunoDAO dao = new AlunoDAO(this);
        List<Aluno> alunos = dao.buscaAlunos();
        dao.close();

        AlunosAdapter adapter = new AlunosAdapter(this, alunos); //em substituição ao arrayAdapter
        listaAlunos.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lista_alunos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_enviar_notas:
                new EnviaAlunosTask(this).execute();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, final ContextMenu.ContextMenuInfo menuInfo) { //cria menu de contexto
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        final Aluno aluno = (Aluno) listaAlunos.getItemAtPosition(info.position);

        MenuItem itemTelefone = menu.add("Eniar SMS");
        Intent intentSMS = new Intent(Intent.ACTION_VIEW);
        intentSMS.setData(Uri.parse("sms:" + aluno.getTelefone()));
        itemTelefone.setIntent(intentSMS);

        MenuItem itemMapa = menu.add("Visualizar no mapa");
        Intent intentMapa = new Intent(Intent.ACTION_VIEW);
        intentMapa.setData(Uri.parse("geo:0,0?q=" + aluno.getEndereco())); //sms: e geo: são os protocolos que o android utiliza
        itemMapa.setIntent(intentMapa);


        MenuItem itemtSite = menu.add("Visitar Site"); //inclui um item no menu, para visitar o site do aluno
        Intent intentSite = new Intent(Intent.ACTION_VIEW); //instancia uma nova intent e informa que deve ser msotrago algo para o usuário (o android decide que app será usado para exibir)
        String site = aluno.getSite(); //repuera o site do aluno
        if (!site.startsWith("http://")) { //valida se o site do aluno ja esta com o porotoloco http (é necessário para que o android identifique o recurso que será aberto)
            site = "http://" + site;
        }
        intentSite.setData(Uri.parse(site)); //transforma a string com o site em um objeto Uri
        itemtSite.setIntent(intentSite); //associa o item do menu com o intent

        MenuItem itemLigar = menu.add("Ligar"); //insere item "ligar"
        itemLigar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (ActivityCompat.checkSelfPermission(ListaAlunosActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) { //verifica se a permissão já foi condedida pelo usuario
                    ActivityCompat.requestPermissions(ListaAlunosActivity.this, new String[] {Manifest.permission.CALL_PHONE},  CODIGO_TELEFONE); //requisita a permissão
                } else {
                    Intent intentLigar = new Intent(Intent.ACTION_CALL);
                    intentLigar.setData(Uri.parse("tel:" + aluno.getTelefone()));
                    startActivity(intentLigar);
                }
                return false;
            }
        });


        MenuItem deletar = menu.add("Deletar"); //insere item "Deletar"
        deletar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                AlunoDAO dao = new AlunoDAO(ListaAlunosActivity.this);
                dao.deleta(aluno);
                dao.close();
                Toast.makeText(ListaAlunosActivity.this, "Deletar o aluno " + aluno.getNome(), Toast.LENGTH_SHORT).show();
                carregaLista();
                return false;
            }
        });
    }
}
