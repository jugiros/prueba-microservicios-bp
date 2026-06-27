package com.bp.msclientes.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Cliente extends Persona {

    private String clienteId;
    private String contrasena;
    private Boolean estado;

    public Cliente(Long id, String nombre, String genero, Integer edad,
                   String identificacion, String direccion, String telefono,
                   String clienteId, String contrasena, Boolean estado) {
        super(id, nombre, genero, edad, identificacion, direccion, telefono);
        this.clienteId = clienteId;
        this.contrasena = contrasena;
        this.estado = estado;
    }

    public static ClienteBuilder builder() {
        return new ClienteBuilder();
    }

    public static class ClienteBuilder {
        private Long id;
        private String nombre;
        private String genero;
        private Integer edad;
        private String identificacion;
        private String direccion;
        private String telefono;
        private String clienteId;
        private String contrasena;
        private Boolean estado;

        public ClienteBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ClienteBuilder nombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        public ClienteBuilder genero(String genero) {
            this.genero = genero;
            return this;
        }

        public ClienteBuilder edad(Integer edad) {
            this.edad = edad;
            return this;
        }

        public ClienteBuilder identificacion(String identificacion) {
            this.identificacion = identificacion;
            return this;
        }

        public ClienteBuilder direccion(String direccion) {
            this.direccion = direccion;
            return this;
        }

        public ClienteBuilder telefono(String telefono) {
            this.telefono = telefono;
            return this;
        }

        public ClienteBuilder clienteId(String clienteId) {
            this.clienteId = clienteId;
            return this;
        }

        public ClienteBuilder contrasena(String contrasena) {
            this.contrasena = contrasena;
            return this;
        }

        public ClienteBuilder estado(Boolean estado) {
            this.estado = estado;
            return this;
        }

        public Cliente build() {
            return new Cliente(id, nombre, genero, edad, identificacion,
                    direccion, telefono, clienteId, contrasena, estado);
        }
    }
}
