package Pert1;
import java.util.Scanner;

public class NilaiMhs {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        String nim, nama, grade;
        double uts, uas, rata;

        System.out.println("data: ");
        System.out.print("nim: "); nim = input.next();
        System.out.print("nama: "); nama = input.next();
        System.out.print("nilai UTS: "); uts = input.nextDouble();
        System.out.print("nilai UAS: "); uas = input.nextDouble();

        rata = (uts + uas) / 2;

        if (rata < 50)
            grade = "E";
        else if (rata < 60)
            grade = "D";
        else if (rata < 70)
            grade = "C";
        else if (rata < 80)
            grade = "B";
        else
            grade = "A";

        System.out.println("===============================================");
        System.out.println("Nim\tNama\tUTS\tUAS\tRata2\tGrade");
        System.out.println("===============================================");
        System.out.println(nim + "\t" + nama + "\t" + uts + "\t" + uas + "\t" + rata + "\t" + grade);

        System.out.println("");
        System.out.println("");
    }
}