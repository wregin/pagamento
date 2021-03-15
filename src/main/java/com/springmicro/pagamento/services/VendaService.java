package com.springmicro.pagamento.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.springmicro.pagamento.data.vo.VendaVO;
import com.springmicro.pagamento.entity.ProdutoVenda;
import com.springmicro.pagamento.entity.Venda;
import com.springmicro.pagamento.exception.ResourceNotFoundException;
import com.springmicro.pagamento.repository.ProdutoVendaRepository;
import com.springmicro.pagamento.repository.VendaRepository;

@Service
public class VendaService {
	
	private final VendaRepository vendaRepository;
	
	private final ProdutoVendaRepository produtoVendaRepository;

	@Autowired
	public VendaService(VendaRepository vendaRepository, ProdutoVendaRepository produtoVendaRepository) {
		this.vendaRepository = vendaRepository;
		this.produtoVendaRepository = produtoVendaRepository;
	}

	public VendaVO create(VendaVO vendaVO) {
		Venda venda = vendaRepository.save(Venda.create(vendaVO));
		
		List<ProdutoVenda> produtosSalvos = new ArrayList<>();
		
		vendaVO.getProdutos().forEach(p -> {
			ProdutoVenda pv = ProdutoVenda.create(p);
			pv.setVenda(venda);
			produtosSalvos.add(produtoVendaRepository.save(pv));
		});
		
		venda.setProdutos(produtosSalvos);
		
		return VendaVO.create(venda);
	}

	public Page<VendaVO> findAll(Pageable pageable) {
		var page = vendaRepository.findAll(pageable);
		return page.map(this::convertVendaVO);
	}

	private VendaVO convertVendaVO(Venda venda) {
		return VendaVO.create(venda);
	}

	public VendaVO findById(Long id) {
		var entity = vendaRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("ID não foi localizado"));
		return VendaVO.create(entity);
	}

	public VendaVO update(VendaVO vendaVO) {
		final Optional<Venda> optionalVenda = vendaRepository.findById(vendaVO.getId()); 

		if (!optionalVenda.isPresent()) {
			new ResourceNotFoundException("Venda não foi localizado");
		}

		return VendaVO.create(vendaRepository.save(Venda.create(vendaVO)));

	}

	public void delete(Long id) {
		var entity = vendaRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("ID não foi localizado"));

		vendaRepository.delete(entity);

	}


}
