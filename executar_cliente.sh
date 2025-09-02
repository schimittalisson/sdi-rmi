#!/bin/bash

echo "=== Iniciando Cliente RMI ==="

# Define o Java 17 explicitamente
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Navega para o diretório src
cd src

# Inicia o cliente RMI com Java 17
echo "Conectando ao servidor RMI com Java 17..."
$JAVA_HOME/bin/java ClienteRMI
