package one.digitalinnovation.gof.service.impl;

import one.digitalinnovation.gof.model.Cliente;
import one.digitalinnovation.gof.model.ClienteRepository;
import one.digitalinnovation.gof.model.Endereco;
import one.digitalinnovation.gof.model.EnderecoRepository;
import one.digitalinnovation.gof.service.ClienteService;
import one.digitalinnovation.gof.service.ViaCepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {

    // Singleton: Injetar os componentes do Spring com @Autowired
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private EnderecoRepository enderecoRepository;
    @Autowired
    private ViaCepService viaCepService;

    // Strategy: Implementar os métodos definidos na interface.
    // Facade: Abstrair interações com subsistemas, provendo uma interface simples.

    @Override
    public Iterable<Cliente> buscarTodos() {
        //  Buscar todos os Clientes.
        return clienteRepository.findAll();
    }

    @Override
    public Cliente buscarPorId(Long id) {
        //  Buscar Cliente por Id.
        // pq Optional: porque ele pode ou não existir
        Optional<Cliente> cliente = clienteRepository.findById(id);
        if(cliente.isPresent()){
            return cliente.get();
        }else {
            return null;
        }
    }

    @Override
    public void inserir(Cliente cliente) {
        salvarClienteComCep(cliente);
    }

    private void salvarClienteComCep(Cliente cliente) {
        //  Verificar se o Endereco do Clinte já existe (pelo CEP).
        String cep = cliente.getEndereco().getCep();
        Endereco endereco = enderecoRepository.findById(cep).orElseGet(() ->{
            //  Caso não exista, integrar com o ViaCEP e persistir o retorno.
            Endereco novoEndereco = viaCepService.consultarCep(cep);
            enderecoRepository.save(novoEndereco);
            return novoEndereco;
        });
        cliente.setEndereco(endereco);
        //  Inserir Cliente, vinculando o Endereco (novo ou existente).
        clienteRepository.save(cliente);
    }

    @Override
    public void atualizar(Long id, Cliente cliente) {
        //  Buscar Cliente por Id, caso exista:
        Optional<Cliente> clienteBd = clienteRepository.findById(id);
        if(clienteBd.isPresent()){
            // Verificar se o Endereco do Cliente já exite (pelo CEP).
            // Caso não exista, integrar com o ViaCEP e persistir o retorno.
            // Alterar Cliente, vinculando o Endereco (novo ou existente).
            salvarClienteComCep(cliente);
        }

    }

    @Override
    public void deletar(Long id) {
        // Deletar o Cliente po ID.
        clienteRepository.deleteById(id);
    }
}
