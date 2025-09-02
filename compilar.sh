#!/bin/bash

echo "=== Compilando sistema RMI ==="

# Define o Java 17 explicitamente
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Navega para o diretório src
cd src

# Compila todas as classes Java com Java 17
echo "Compilando interfaces e implementações com Java 17..."
$JAVA_HOME/bin/javac *.java

if [ $? -eq 0 ]; then
    echo "✅ Compilação concluída com sucesso!"
    echo ""
    echo "Para executar o sistema:"
    echo "1. Execute: ./executar_servidor.sh"
    echo "2. Em outro terminal, execute: ./executar_cliente.sh"
else
    echo "❌ Erro na compilação!"
    exit 1
fi
