package br.com.alura.agenda.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

import br.com.alura.agenda.R;
import br.com.alura.agenda.dao.AlunoDAO;

/**
 * Created by Parafuso Solto on 14/03/2017.
 */

public class SMSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Object[] pdus = (Object[]) intent.getSerializableExtra("pdus"); //PDU = protocol Data Unit, é o protocolo com o qual os dados de SMS trafegam. Nesse caso, pode vir uma única, ou várias, dependendo do tamanho da mensagem
        byte[] pdu = (byte[]) pdus[0]; //toda pdu tem o número de telefone, então extraímos uma única pdu para podermos trabalhar e a convertemos em um array de bytes (se quisessemos a mensagen, precisariamos de todas as pdus)

        String formato = (String) intent.getSerializableExtra("format"); //temos que informar o formato da pdu ao método createfromPdu.

        SmsMessage sms = SmsMessage.createFromPdu(pdu, formato);

        String telefone = sms.getDisplayOriginatingAddress(); //captura o telefone

        AlunoDAO dao = new AlunoDAO(context);

        if(dao.alunoExiste(telefone)){ //pede ao dao que informe se o telefone é de algum dos alunos cadastrados
            Toast.makeText(context, "SMS recebido", Toast.LENGTH_SHORT).show();
            MediaPlayer mp = MediaPlayer.create(context, R.raw.msg);
            mp.start();
        }
        dao.close();
    }
}
