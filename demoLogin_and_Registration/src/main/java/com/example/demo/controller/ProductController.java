package com.example.demo.controller;

import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.impl.ProductServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductServiceImpl service;

    @Autowired
    private ProductRepository repository;

//    private List<Product> listProducts;

//    @RequestMapping("/")
//    public String viewHomePage(Model model,@Param("keyword") String keyword) {
//        List<Product> listProducts = service.listAll(keyword);
//        model.addAttribute("listProducts", listProducts);
//        model.addAttribute("keyword", keyword);
//        return "product";
//    }

//    @RequestMapping("/")
//    public String viewHomePage(Model model) {
//        List<Product> listProducts = service.findAll();
//        model.addAttribute("listProducts", listProducts);
//
//        return "product";
//    }

    @RequestMapping("/new")
    public String showNewProductForm(Model model) {
        Product product = new Product();
        model.addAttribute("product", product);

        return "new_product";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveProduct(@ModelAttribute("product") Product product) {
        service.save(product);

        return "redirect:/product/";
    }

    @RequestMapping("/edit/{id}")
    public ModelAndView showEditProductForm(@PathVariable(name = "id") Long id) {
        ModelAndView mav = new ModelAndView("edit_product");

        Product product = service.getOne(id);
        mav.addObject("product", product);

        return mav;
    }

    @RequestMapping("/delete/{id}")
    public String deleteProduct(@PathVariable(name = "id") Long id) {
        service.delete(id);
        return "redirect:/product/";
    }

//   @RequestMapping("/")
//   public String getProducts(@RequestParam(name = "pageNumber", defaultValue = "1", required = false) Integer pageNumber,
//                             @RequestParam(name = "keyword", required = false) String ten,
//                             Model model) {
//
//       Page<Product> productPage;
//       Pageable pageable = PageRequest.of(pageNumber - 1, 2);
//       if (ten == null || ten.isBlank()) {
//           productPage = repository.findAll(pageable);
//       } else {
//           productPage = service.findProductByName(ten, pageable);
//       }
//
//       model.addAttribute("listProducts", productPage);
//       return "product";
//   }
//
//    @RequestMapping("/")
//    public String searchProducts(@RequestParam(name = "keyword", required = false) String keyword,
//                                 @RequestParam(name = "page", defaultValue = "0") int page,
//                                 @RequestParam(name = "size", defaultValue = "2") int size,
//                                 Model model) {
//        Page<Product> productPage;
//        Pageable pageable = PageRequest.of(page, size);
//
//        if (keyword == null || keyword.isBlank()) {
//           productPage = repository.findAll(pageable);
//       } else {
//            productPage = service.searchProducts(keyword, pageable);
//        }
//        model.addAttribute("listProducts", productPage);
//
//        return "product";
//    }

    @RequestMapping("/")
    public String viewHomePage(Model model) {
        return viewPage(model, 1, "name", "asc");
    }

    @RequestMapping("/page/{pageNum}")
    public String viewPage(Model model,
                           @PathVariable(name = "pageNum") int pageNum,
                           @Param("sortField") String sortField,
                           @Param("sortDir") String sortDir) {

        Page<Product> page = service.listAll(pageNum, sortField, sortDir);

        List<Product> listProducts = page.getContent();

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        model.addAttribute("listProducts", listProducts);

        return "product";
    }

}
