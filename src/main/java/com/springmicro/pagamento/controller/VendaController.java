package com.springmicro.pagamento.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springmicro.pagamento.data.vo.VendaVO;
import com.springmicro.pagamento.services.VendaService;

@RestController
@RequestMapping("/venda")
public class VendaController {
	
	// para mostrar que havendo o balance
	@Value("${server.port}")
	String  porta;

	private final VendaService vendaService;

	private final PagedResourcesAssembler<VendaVO> assembler;

	@Autowired
	public VendaController(VendaService vendaService, PagedResourcesAssembler<VendaVO> assembler) {

		this.vendaService = vendaService;
		this.assembler = assembler;
	}
	
	// aqui é o método que vvai retornar a porta, ideial chamar a request várias X para mostrar a porta, no caso o balance
	@RequestMapping("/mostrarporta")
	public String mostrarPorta() {
		return porta;
	}

	@GetMapping(value = "/{id}", produces = { "application/json", "application/xml", "application/x-yaml" })
	public VendaVO findById(@PathVariable("id") Long id) {

		VendaVO vendaVO = vendaService.findById(id);

		vendaVO.add(linkTo(methodOn(VendaController.class).findById(id)).withSelfRel());

		return vendaVO;
	}

	@GetMapping(produces = { "application/json", "application/xml", "application/x-yaml" })
	public ResponseEntity<?> findAll(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "limit", defaultValue = "12") int limit,
			@RequestParam(value = "direction", defaultValue = "asc") String direction) {

		var sorDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
		
		Pageable pageable = PageRequest.of(page, limit, Sort.by(sorDirection, "data"));
		
		Page<VendaVO> vendas = vendaService.findAll(pageable);
		vendas.stream()
				.forEach(p -> p.add(linkTo(methodOn(VendaController.class).findById(p.getId())).withSelfRel()));
		
		// ele vai retornar já paginado para o front, angular por exemplo, dai vem o link da 1 pagina, 2 e por ai vai
		PagedModel<EntityModel<VendaVO>> pagedModel = assembler.toModel(vendas);
		
		return new ResponseEntity<>(pagedModel, HttpStatus.OK);
		
	}
	
	@PostMapping(produces = { "application/json", "application/xml", "application/x-yaml" }, 
			consumes = { "application/json", "application/xml", "application/x-yaml" } )
	public VendaVO create(@RequestBody VendaVO vendaVO) {
		VendaVO proVO = vendaService.create(vendaVO);
		
		proVO.add(linkTo(methodOn(VendaController.class).findById(proVO.getId())).withSelfRel());
		
		return proVO;
	}
	
	@PutMapping(produces = { "application/json", "application/xml", "application/x-yaml" }, 
			consumes = { "application/json", "application/xml", "application/x-yaml" } )
	public VendaVO update(@RequestBody VendaVO vendaVO) {
		VendaVO proVO = vendaService.update(vendaVO);
		
		proVO.add(linkTo(methodOn(VendaController.class).findById(vendaVO.getId())).withSelfRel());
		
		return proVO;
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable("id") Long id) {
		vendaService.delete(id);
		return ResponseEntity.ok().build();
	}
	


}
