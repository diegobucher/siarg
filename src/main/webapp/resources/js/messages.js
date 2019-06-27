var Messages = {
	MA002: 'Informe os dados obrigatórios.',
	MA005: 'O prazo de atendimento da unidade Externa é apenas uma estimativa.',
	MA006: 'Demanda incluída com sucesso.',
	MA007: 'Rascunho salvo com sucesso.',
	MA008: 'Contate seu gestor para encaminhar a demanda.',
	MA009: 'Nenhum Registro Encontrado.',
	MA010: '',
	MA011: 'Prazo informado inválido.',
	MA012: 'Arquivo Inválido. Somente é possível anexar arquivos .ZIP.',
	MA013: 'O arquivo anexo teve ter no máximo 2mb.',
	MA014: '',
	MA015: 'Informe ao menos dois caracteres.',
	MA016: 'Selecione ao menos uma Caixa Postal de destino.',
	MA033: 'Data inválida.',	
	MA040: 'Data de Publicação não pode ser superior a data de Expiração.',
	MA041: 'Data de Publicação não pode ser igual à data de Expiração.',
	MA042: 'Data de Publicação não pode ser inferior à data atual.', 
	
	
	/* métodos */
	get: function (msg) {
		var str = eval('Messages.' + msg)
		/** parâmetros */
		for (var i = 1; i < arguments.length; i++) {
			str = str.replace('?', arguments[i])
		}
		
		return str
	}
}