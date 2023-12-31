package mySecretPhoneBook;

public class Contact implements java.io.Serializable, Comparable<Contact> {
    private String nome;
    private String cognome;
    private String numero;

    public Contact() {
    }

    public Contact(String nome, String cognome, String numero) {
        this.nome = nome;
        this.cognome = cognome;
        this.numero = numero;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((nome == null) ? 0 : nome.hashCode());
        result = prime * result + ((cognome == null) ? 0 : cognome.hashCode());
        result = prime * result + ((numero == null) ? 0 : numero.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        Contact other = (Contact) obj;
        return nome.equals(other.getNome()) && cognome.equals(other.getCognome()) && numero.equals(other.getNumero());
    }

    @Override
    public int compareTo(Contact o) {
        return nome.compareTo(o.getNome());
    }

    @Override
    public String toString() {
        return nome + " " + cognome + " " + numero;
    }

}
