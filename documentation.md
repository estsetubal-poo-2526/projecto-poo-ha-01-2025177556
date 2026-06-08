# Documentação do Projeto — Jogo da Memória

Este documento descreve as classes que compõem o jogo da memória, explicando o que cada uma faz e de que forma contribui para o funcionamento geral do jogo.

---

## `EstadoCarta`

É um `enum` que representa os três estados possíveis da carta no tabuleiro.

- `ESCONDIDA` — a carta está virada para baixo e o jogador não vê a imagem.
- `VISIVEL` — a carta foi clicada e está temporariamente a mostrar a imagem.
- `ENCONTRADA` — a carta faz parte de um par já descoberto e fica permanentemente revelada.

---

## `EstadoJogo`

É um `enum` que representa o estado atual da partida.

- `EM_ANDAMENTO` — o jogo ainda está a decorrer.
- `VITORIA` — o jogador encontrou todos os pares antes de esgotar as tentativas.
- `DERROTA` — o jogador ficou sem tentativas antes de encontrar todos os pares.

---

## `Modo`

É um `enum` que representa os dois modos de jogo disponíveis.

- `NUMEROS` — as cartas mostram números no lado da frente.
- `CARTAS` — as cartas mostram imagens carregadas a partir dos recursos do projeto.

---

## `Efeito`

É uma `interface` que define um comportamento especial que uma carta pode ter quando o seu par é encontrado. Quem implementar esta interface tem de fornecer o método `aplicarEfeito(JogoMemoria jogo)`, que recebe o jogo como argumento para poder modificar o seu estado (por exemplo, adicionar tentativas).

---

## `Carta`

É uma classe `abstract` que representa uma carta genérica do tabuleiro. Não pode ser instanciada diretamente — serve de base para `CartaNormal` e `CartaBonus`.

Guarda o identificador da imagem (`idImagem`) que permite comparar duas cartas para saber se formam um par, e o estado atual da carta (`EstadoCarta`).

Tem os seguintes métodos:

- `revelar()` — muda o estado de `ESCONDIDA` para `VISIVEL`, mas só se a carta estiver escondida.
- `esconder()` — muda o estado de `VISIVEL` para `ESCONDIDA`, mas só se a carta estiver visível.
- `marcarComoParEncontrado()` — muda o estado para `ENCONTRADA`, independentemente do estado anterior.
- `getIdImagem()` e `getEstado()` — devolvem o identificador e o estado atual da carta.

---

## `CartaNormal`

Estende `Carta` e implementa `Efeito`. Representa uma carta comum sem efeito especial.

O método `aplicarEfeito()` está implementado mas não faz nada (`return` imediato), porque uma carta normal não tem qualquer efeito ao ser descoberta.

---

## `CartaBonus`

Estende `Carta` e implementa `Efeito`. Representa uma carta especial que recompensa o jogador quando o seu par é encontrado.

O método `aplicarEfeito()` chama `jogo.adicionarTentativas(2)`, dando ao jogador duas tentativas extra. No modo `NUMEROS`, estas cartas correspondem ao identificador `1` e aparecem com fundo dourado e o texto "Bónus".

---

## `Tabuleiro`

Representa o tabuleiro de jogo, que tem sempre 4 linhas por 5 colunas (20 cartas no total, ou seja, 10 pares).

Quando é criado, recebe o `Modo` de jogo e inicializa as cartas de acordo com ele:

- No modo `NUMEROS`, cria 10 pares de `CartaNormal` com identificadores de 1 a 10. Em seguida, substitui os dois exemplares com identificador `1` por `CartaBonus`.
- No modo `CARTAS`, tenta encontrar imagens `.png` nos recursos do projeto. Separa uma imagem especial (o ás) para ser o par bónus e escolhe aleatoriamente 9 imagens para os restantes pares normais. Se não encontrar imagens, usa nomes de cartas de baralho como alternativa.

Após a inicialização, as cartas são sempre baralhadas aleatoriamente, tanto a lista de cartas como a lista de caminhos de imagem correspondentes, para que a posição das cartas seja diferente em cada jogo.

Métodos relevantes:

- `getCarta(linha, coluna)` — devolve a carta numa determinada posição do tabuleiro.
- `getImagemPath(linha, coluna)` — devolve o caminho da imagem associada à carta nessa posição (pode ser `null` no modo `NUMEROS`).
- `todosParesEncontrados()` — percorre todas as cartas e devolve `true` apenas se todas estiverem no estado `ENCONTRADA`.

---

## `JogoMemoria`

É a classe central que gere a lógica do jogo. Coordena o tabuleiro, as tentativas e as jogadas do utilizador.

Guarda o tabuleiro, o número de tentativas restantes (começa em 20), as duas cartas atualmente viradas, o estado do jogo e o modo escolhido.

Métodos principais:

- `iniciarNovoJogo(Modo modo)` — reinicia tudo: cria um novo tabuleiro, repõe as 20 tentativas, limpa as cartas viradas e coloca o estado em `EM_ANDAMENTO`.
- `fazerJogada(int linha, int coluna)` — processa o clique do jogador numa carta. Se for a primeira carta da jogada, simplesmente revela-a. Se for a segunda, revela-a, desconta uma tentativa e compara os identificadores das duas cartas:
  - Se forem iguais (par encontrado), ambas são marcadas como `ENCONTRADA` e o efeito da carta é aplicado (se houver). O jogo verifica depois se há vitória.
  - Se forem diferentes, as cartas ficam visíveis temporariamente para o jogador ver — a interface trata depois de as esconder chamando `esconderParesSelecionadosIfMismatch()`.
- `esconderParesSelecionadosIfMismatch()` — esconde as duas cartas viradas que não formaram par e verifica se o jogador perdeu (tentativas esgotadas).
- `adicionarTentativas(int i)` — adiciona tentativas ao contador, usado pelo efeito de `CartaBonus`.

---

## `App`

É a classe principal da interface gráfica, que estende `Application` do JavaFX. É o ponto de entrada do programa (contém o método `main`).

Trata de toda a parte visual e das interações com o utilizador. As suas responsabilidades são:

- Mostrar o menu inicial com a escolha do modo de jogo (`Números` ou `Cartas`) e os botões de início e saída.
- Construir a grelha do jogo com base no tabuleiro devolvido por `JogoMemoria`, criando um `StackPane` para cada carta.
- Gerir as animações de virar carta (flip 3D com `RotateTransition`) quando o utilizador clica numa carta.
- Coordenar o fluxo de jogada com a lógica do jogo: chama `fazerJogada()` após a animação, aguarda 800 milissegundos em caso de falha e chama `esconderParesSelecionadosIfMismatch()` para devolver as cartas ao estado escondido.
- Atualizar o contador de tentativas visível no ecrã após cada jogada.
- Mostrar uma janela de alerta (`Alert`) no fim do jogo, tanto em caso de vitória como de derrota, e voltar ao menu automaticamente quando o jogador fechar essa janela.
- Ajustar dinamicamente o tamanho das cartas quando a janela é redimensionada.

---

## Classes de Teste

Os testes estão organizados em três ficheiros, cada um focado numa classe diferente.

**`CartaTest`** — testa o ciclo de vida de uma carta: criação com o estado `ESCONDIDA`, transição para `VISIVEL` ao revelar, retorno a `ESCONDIDA` ao esconder, e transição para `ENCONTRADA` ao marcar como par encontrado.

**`TabuleiroTest`** — verifica que o tabuleiro no modo `NUMEROS` tem as dimensões corretas (4 linhas, 5 colunas), que todas as posições têm uma carta, que os caminhos de imagem são `null` neste modo, e que `todosParesEncontrados()` devolve `false` no início do jogo.

**`JogoMemoriaTest`** — encontra programaticamente um par de cartas que não seja bónus, executa as duas jogadas e confirma que o par fica marcado como `ENCONTRADA`, que `ultimaJogadaFoiMatch()` devolve `true` e que o número de tentativas decrementou corretamente.