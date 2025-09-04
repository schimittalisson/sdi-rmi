# Sistema RMI - Restaurante e Cozinha

Este projeto implementa um sistema distribuído usando Java RMI (Remote Method Invocation) para simular as operações de um restaurante e sua cozinha.

## Estrutura do Projeto

```
src/
├── Restaurante.java        # Interface do serviço Restaurante
├── RestauranteImpl.java    # Implementação do serviço Restaurante
├── Cozinha.java           # Interface do serviço Cozinha
├── CozinhaImpl.java       # Implementação do serviço Cozinha
├── ServidorRMI.java       # Servidor que registra os serviços
├── ClienteRMI.java        # Cliente para testar os serviços
└── Main.java              # (desconsiderado conforme solicitado)
```

## Funcionalidades

### Serviço Restaurante
- **novaComanda()**: Cria uma nova comanda para um cliente
- **consultarCardapio()**: Retorna o cardápio disponível
- **fazerPedido()**: Adiciona itens a uma comanda
- **valorComanda()**: Consulta o valor total de uma comanda
- **fecharComanda()**: Finaliza e remove uma comanda

### Serviço Cozinha
- **novoPreparo()**: Inicia o preparo de um pedido
- **tempoPreparo()**: Consulta tempo restante de preparo
- **pegarPreparo()**: Retira o pedido quando pronto

## Como Executar

### 1. Compilar o projeto
```bash
./compilar.sh
```

### 2. Iniciar o servidor RMI
```bash
./executar_servidor.sh
```

### 3. Executar Mesa

Em outro terminal, execute:

```bash
java -cp src Mesa
```

## Formato dos Dados

Os dados seguem o formato CSV conforme especificado:
- **Formato**: `codigo,produto,valor`
- **Exemplo**: `1,Pizza de Calabresa,14.19`

## Exemplo de Uso

1. **Cliente chega**: Nova comanda é criada
2. **Consulta cardápio**: Lista de produtos disponíveis
3. **Faz pedido**: Adiciona itens à comanda
4. **Pedido vai para cozinha**: Inicia preparo com tempo aleatório (1-10s)
5. **Monitora preparo**: Consulta tempo restante
6. **Retira pedido**: Quando pronto, pedido é entregue
7. **Paga conta**: Consulta valor e fecha comanda

## Características Técnicas

- **RMI Registry**: Porta 1099 (padrão)
- **Tempo de preparo**: Aleatório entre 1-10 segundos
- **Persistência**: Em memória (dados perdidos ao reiniciar)
- **Concorrência**: Suporta múltiplos clientes simultâneos
