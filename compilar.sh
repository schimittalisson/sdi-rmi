#!/bin/bash

echo "=== Compilando sistema RMI ==="

# Usa o Java padrão do sistema (Java 8)
echo "Compilando interfaces e implementações com Java padrão..."

# Navega para o diretório src
cd src

# Compila todas as classes Java
javac *.java

if [ $? -eq 0 ]; then
    echo "✅ Compilação concluída com sucesso!"
    echo ""
    echo "Para executar o sistema:"
    echo "1. Execute: ./executar_servidor_simples.sh"
    echo "2. Em outro terminal, execute: java -cp src Mesa"
else
    echo "❌ Erro na compilação!"
    exit 1
fi
