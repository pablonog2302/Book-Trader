const modalApp = document.createElement('dialog');
modalApp.className = 'app-modal';
modalApp.setAttribute('aria-labelledby', 'appModalTitulo');
modalApp.setAttribute('aria-describedby', 'appModalMensagem');
modalApp.innerHTML = `
    <section class="app-modal-card">
        <p id="appModalIcone" class="app-modal-icon" aria-hidden="true"></p>
        <h2 id="appModalTitulo"></h2>
        <p id="appModalMensagem"></p>
        <p id="appModalOrientacao" class="app-modal-guidance"></p>
        <label id="appModalRotulo" for="appModalCampo"></label>
        <textarea id="appModalCampo" rows="4"></textarea>
        <p id="appModalValidacao" class="app-modal-validation" role="alert"></p>
        <div class="app-modal-actions">
            <button id="appModalCancelar" class="button-secondary" type="button">Cancelar</button>
            <button id="appModalConfirmar" type="button">Fechar</button>
        </div>
    </section>
`;
document.body.appendChild(modalApp);

// a promessa permite aguardar a decisao e centraliza o fechamento com restauracao do foco anterior.
function abrirModalAplicacao({
                                 tipo,
                                 titulo,
                                 mensagem,
                                 orientacao = '',
                                 confirmar = 'Fechar',
                                 cancelar = 'Cancelar',
                                 campo = false,
                                 rotulo = '',
                                 placeholder = '',
                                 maxLength = null,
                             }) {
    const tituloElemento = document.getElementById('appModalTitulo');
    const mensagemElemento = document.getElementById('appModalMensagem');
    const orientacaoElemento = document.getElementById('appModalOrientacao');
    const iconeElemento = document.getElementById('appModalIcone');
    const campoElemento = document.getElementById('appModalCampo');
    const rotuloElemento = document.getElementById('appModalRotulo');
    const validacaoElemento = document.getElementById('appModalValidacao');
    const botaoCancelar = document.getElementById('appModalCancelar');
    const botaoConfirmar = document.getElementById('appModalConfirmar');
    const focoAnterior = document.activeElement;

    tituloElemento.textContent = titulo;
    mensagemElemento.textContent = mensagem;
    orientacaoElemento.textContent = orientacao;
    orientacaoElemento.hidden = !orientacao;
    iconeElemento.textContent = tipo === 'success' ? '✓' : tipo === 'error' ? '!' : '?';
    iconeElemento.className = `app-modal-icon app-modal-icon-${tipo}`;
    campoElemento.hidden = !campo;
    rotuloElemento.hidden = !campo;
    validacaoElemento.hidden = true;
    campoElemento.value = '';
    campoElemento.placeholder = placeholder;
    rotuloElemento.textContent = rotulo;
    campoElemento.removeAttribute('maxlength');
    if (Number.isInteger(maxLength) && maxLength > 0) {
        campoElemento.maxLength = maxLength;
    }

    const precisaCancelar = tipo === 'confirm' || tipo === 'reason';
    botaoCancelar.hidden = !precisaCancelar;
    botaoCancelar.textContent = cancelar;
    botaoConfirmar.textContent = confirmar;

    return new Promise((resolve) => {
        let finalizado = false;
        const concluir = (resultado) => {
            if (finalizado) {
                return;
            }
            finalizado = true;
            modalApp.oncancel = null;
            if (modalApp.open) {
                modalApp.close();
            }
            if (focoAnterior && focoAnterior.isConnected) {
                focoAnterior.focus();
            }
            resolve(resultado);
        };

        botaoCancelar.onclick = () => concluir(tipo === 'reason' ? null : false);
        botaoConfirmar.onclick = () => {
            if (campo) {
                const valor = campoElemento.value.trim();
                if (!valor) {
                    validacaoElemento.textContent = 'Este campo é obrigatório.';
                    validacaoElemento.hidden = false;
                    campoElemento.focus();
                    return;
                }
                concluir(valor);
                return;
            }
            concluir(true);
        };
        modalApp.oncancel = (evento) => {
            evento.preventDefault();
            concluir(tipo === 'reason' ? null : false);
        };

        modalApp.showModal();
        if (campo) {
            campoElemento.focus();
        } else {
            botaoConfirmar.focus();
        }
    });
}

function mostrarSucesso(mensagem, titulo = 'Operação concluída') {
    return abrirModalAplicacao({
        tipo: 'success',
        titulo,
        mensagem,
        confirmar: 'Fechar',
    });
}

function mostrarErro(mensagem, orientacao = '') {
    return abrirModalAplicacao({
        tipo: 'error',
        titulo: 'Não foi possível concluir',
        mensagem: typeof mensagem === 'string' && mensagem.trim()
            ? mensagem
            : 'Ocorreu um problema inesperado. Tente novamente mais tarde.',
        orientacao,
        confirmar: 'Fechar',
    });
}

function confirmarAcao(mensagem, titulo = 'Confirmar ação', confirmar = 'Confirmar') {
    return abrirModalAplicacao({
        tipo: 'confirm',
        titulo,
        mensagem,
        confirmar,
        cancelar: 'Voltar',
    });
}

function solicitarMotivo(mensagem, titulo = 'Informe o motivo') {
    return abrirModalAplicacao({
        tipo: 'reason',
        titulo,
        mensagem,
        confirmar: 'Continuar',
        cancelar: 'Cancelar',
        campo: true,
        rotulo: 'Motivo',
        placeholder: 'Descreva brevemente o motivo.',
    });
}
