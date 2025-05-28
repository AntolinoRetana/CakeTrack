package com.example.caketrack.Admin.Reservas.Moduls;

public class Reserva {
        private String id;
        private String clienteId;
        private String clienteNombre;
        private String pastelId;
        private String pastelNombre;
        private String fecha;
        private String notas;

        public Reserva() {}

        public Reserva(String id, String clienteId, String clienteNombre, String pastelId, String pastelNombre, String fecha, String notas) {
            this.id = id;
            this.clienteId = clienteId;
            this.clienteNombre = clienteNombre;
            this.pastelId = pastelId;
            this.pastelNombre = pastelNombre;
            this.fecha = fecha;
            this.notas = notas;
        }

        public String getClienteId() {
            return clienteId;
        }

        public void setClienteId(String clienteId) {
            this.clienteId = clienteId;
        }

        public String getClienteNombre() {
            return clienteNombre;
        }

        public void setClienteNombre(String clienteNombre) {
            this.clienteNombre = clienteNombre;
        }

        public String getFecha() {
            return fecha;
        }

        public void setFecha(String fecha) {
            this.fecha = fecha;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getNotas() {
            return notas;
        }

        public void setNotas(String notas) {
            this.notas = notas;
        }

        public String getPastelId() {
            return pastelId;
        }

        public void setPastelId(String pastelId) {
            this.pastelId = pastelId;
        }

        public String getPastelNombre() {
            return pastelNombre;
        }

        public void setPastelNombre(String pastelNombre) {
            this.pastelNombre = pastelNombre;
        }
    }
