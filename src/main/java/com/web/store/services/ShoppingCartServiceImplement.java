package com.web.store.services;

import com.web.store.entity.CartItem;
import com.web.store.entity.ShoppingCart;
import com.web.store.entity.User;
import com.web.store.repository.ShoppingCartRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional
public class ShoppingCartServiceImplement implements ShoppingCartService {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Override
    @Transactional
    public Iterable<ShoppingCart> findAll() {
        return shoppingCartRepository.findAll();
    }

    @Override
    @Transactional
    public Optional<ShoppingCart> findById(Long id) {
        return shoppingCartRepository.findById(id);
    }

    @Override
    @Transactional
    public ShoppingCart save(ShoppingCart shoppingCart) {
        return shoppingCartRepository.save(shoppingCart);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        shoppingCartRepository.deleteById(id);
    }

    // Generate invoice
    public byte[] generateInvoice(Long id) throws IOException {

        Optional<ShoppingCart> currentShoppingCart = shoppingCartRepository.findById(id);

        if (!currentShoppingCart.isPresent()) {
            return new byte[0];
        }

        ShoppingCart shoppingCart = currentShoppingCart.get();
        User user = shoppingCart.getUser();

        PDDocument document = new PDDocument();

        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
        contentStream.newLineAtOffset(50, 750);
        contentStream.showText("Purchase Invoice");
        contentStream.newLineAtOffset(0, -20);
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        contentStream.showText("Date: " + LocalDate.now());
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Customer: " + user.getFirstsName() + " " + user.getLastName());
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Products: ");

        for (CartItem item : shoppingCart.getItems()) {
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("- " + item.getProduct().getProductName() + ", Price: "
                    + item.getProduct().getPrice() + ", quantity: " + item.getQuantity());

        }

        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Total: $" + shoppingCart.calculateTotal());

        contentStream.endText();

        contentStream.close();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        document.save(byteArrayOutputStream);
        document.close();

        return byteArrayOutputStream.toByteArray();
    }

    // view invoice content - PDFBOX
    @Override
    public String invoiceContent(Long id) throws IOException {

        byte[] contentPDF = generateInvoice(id);

        PDDocument document = PDDocument.load(new ByteArrayInputStream(contentPDF));

        StringWriter writer = new StringWriter();

        PDFTextStripper stripper = new PDFTextStripper();

        stripper.writeText(document, writer);
        document.close();

        return writer.toString();
    }
}
