package sg.edu.np.mad.pennywise;

import com.google.firebase.auth.FirebaseAuth;
import com.google.type.DateTime;

public class TransferObject {
    private String paymentType;
    private double amount;
    private DateTime dateTime;
    private FirebaseAuth auth;
    private String email;
}
