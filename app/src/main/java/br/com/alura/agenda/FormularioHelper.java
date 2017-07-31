package br.com.alura.agenda;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import br.com.alura.agenda.modelo.Aluno;

/**
 * Created by Parafuso Solto on 15/02/2017.
 */

public class FormularioHelper {

    private EditText campoNome;
    private final EditText campoTelefone;
    private final EditText campoEndereco;
    private final EditText campoSite;
    private final RatingBar campoNota;
    private final ImageView campoFoto;

    private Aluno aluno;
    //Um atributo final de uma classe pode ter seu valor atribuído uma única vez, seja na própria declaração ou no construtor.

    public FormularioHelper(FormularioActivity activity) {     //sobreescrita do construtor

        campoNome = (EditText) activity.findViewById(R.id.formulario_nome);
        campoTelefone = (EditText) activity.findViewById(R.id.formulario_telefone);
        campoEndereco = (EditText) activity.findViewById(R.id.formulario_endereco);
        campoSite = (EditText) activity.findViewById(R.id.formulario_site);
        campoNota = (RatingBar) activity.findViewById(R.id.formulario_nota);
        campoFoto = (ImageView) activity.findViewById(R.id.formulario_foto);

        aluno = new Aluno();
    }

    public Aluno pegaAluno() {
        aluno.setNome(campoNome.getText().toString());
        aluno.setEndereco(campoEndereco.getText().toString());
        aluno.setTelefone(campoTelefone.getText().toString());
        aluno.setSite(campoSite.getText().toString());
        aluno.setNota(Double.valueOf(campoNota.getProgress()));
        aluno.setCaminhoFoto((String) campoFoto.getTag());
        return aluno;
    }

    public void preencheFormulario(Aluno aluno) {
        this.aluno = aluno; //atribui o aluno recebido como o atributo aluno dessa classe

        campoNome.setText(aluno.getNome());
        campoTelefone.setText(aluno.getTelefone());
        campoEndereco.setText(aluno.getEndereco());
        campoSite.setText(aluno.getSite());
        campoNota.setProgress(aluno.getNota().intValue());
        carregaImagem(aluno.getCaminhoFoto());
    }

    public void carregaImagem(String caminhofoto) {
        if(caminhofoto != null) { //se o preencheformulario foi quem chamou esse método e o aluno a ser carregado não possui uma caminho de foto no banco, não há o que carregar
            Bitmap bitmap = BitmapFactory.decodeFile(caminhofoto); //transforma o caminho da imagem salva em uma imagem bitmap
            Bitmap bitmapReduzido = Bitmap.createScaledBitmap(bitmap, 300, 300, true); //redimensiona a imagem para 300x300, porque o ImageView não suporta imagens muito grandes
            campoFoto.setImageBitmap(bitmapReduzido); //insere a imagem no formulario
            campoFoto.setScaleType(ImageView.ScaleType.FIT_XY);//a imagem se ajusta ao tamanho da ImageView
            campoFoto.setTag(caminhofoto); //inclui uma tag no objeto campoFoto contendo o objeto caminhoFoto para que seja recuperado depois
        }
    }
}
