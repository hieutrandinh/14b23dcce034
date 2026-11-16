package org.example.cinema.web;

import org.example.cinema.dao.CounterPaymentDAO;
import org.example.cinema.dao.CustomerStatDAO;
import org.example.cinema.dao.InvoiceDAO;
import org.example.cinema.dao.MemberCardDAO;
import org.example.cinema.dao.MovieDAO;
import org.example.cinema.dao.MovieStatDAO;
import org.example.cinema.dao.ScreeningRoomDAO;
import org.example.cinema.dao.SeatDAO;
import org.example.cinema.dao.ShowTimeDAO;
import org.example.cinema.dao.TicketDAO;
import org.example.cinema.entity.CounterPayment;
import org.example.cinema.entity.Invoice;
import org.example.cinema.entity.MemberCard;
import org.example.cinema.entity.Movie;
import org.example.cinema.entity.ScreeningRoom;
import org.example.cinema.entity.Seat;
import org.example.cinema.entity.Seller;
import org.example.cinema.entity.ShowTime;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Module 2: Seller sells a ticket at the counter. One servlet, the `action`
 * request parameter drives the wizard across the 8 screens of the design:
 * seller home -> search movie -> select show time -> select room -> select seat
 * -> confirm sale -> payment -> display result.
 */
@WebServlet("/sell")
public class SellTicketServlet extends HttpServlet {

    private final MovieDAO movieDAO = new MovieDAO();
    private final ScreeningRoomDAO screeningRoomDAO = new ScreeningRoomDAO();
    private final ShowTimeDAO showTimeDAO = new ShowTimeDAO();
    private final SeatDAO seatDAO = new SeatDAO();
    private final TicketDAO ticketDAO = new TicketDAO();
    private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private final CounterPaymentDAO paymentDAO = new CounterPaymentDAO();
    private final MemberCardDAO memberCardDAO = new MemberCardDAO();
    private final CustomerStatDAO customerStatDAO = new CustomerStatDAO();
    private final MovieStatDAO movieStatDAO = new MovieStatDAO();
    private final Random random = new Random();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        process(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        process(request, response);
    }

    private void process(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        Seller seller = (Seller) session.getAttribute("currentSeller");
        if (seller == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        HttpSession cart = session;
        initializeSaleState(cart);

        String action = request.getParameter("action");
        if (action == null) {
            action = "start";
        }

        try {
            switch (action) {
                case "start":
                    resetSelection(cart);
                    forwardSearch(request, response, null);
                    break;
                case "search":
                    forwardSearch(request, response, request.getParameter("movieName"));
                    break;
                case "selectMovie":
                    selectMovie(request, response, cart);
                    break;
                case "searchShowTime":
                    searchShowTime(request, response, cart);
                    break;
                case "backToSearch":
                    seatIds(cart).clear();
                    forwardSearch(request, response, null);
                    break;
                case "selectRoom":
                    selectRoom(request, response, cart);
                    break;
                case "backToRoom":
                    seatIds(cart).clear();
                    showRoomList(request, response, cart);
                    break;
                case "selectShowTime":
                    selectShowTime(request, response, cart);
                    break;
                case "backToShowTime":
                    showShowTimeList(request, response, cart);
                    break;
                case "selectSeat":
                    toggleSeat(request, response, cart, true);
                    break;
                case "deleteSeat":
                    toggleSeat(request, response, cart, false);
                    break;
                case "continueSeat":
                    forwardConfirmSell(request, response, cart);
                    break;
                case "backToSeat":
                    seatIds(cart).clear();
                    showSeatMap(request, response, cart);
                    break;
                case "checkCard":
                    checkCard(request, response, cart);
                    break;
                case "confirmSell":
                    forwardPayment(request, response, cart);
                    break;
                case "confirmPayment":
                    confirmPayment(request, response, cart, seller);
                    break;
                case "finish":
                    resetSelection(cart);
                    response.sendRedirect(request.getContextPath() + "/seller/ViewHome.jsp");
                    break;
                default:
                    forwardSearch(request, response, null);
            }
        } catch (SQLException | IllegalArgumentException e) {
            getServletContext().log("Sell ticket action failed: " + action, e);
            if ("confirmSell".equals(action) || "confirmPayment".equals(action)) {
                seatIds(cart).clear();
            }
            request.setAttribute("flowError", userMessage(e, action));
            try {
                redisplayCurrentScreen(request, response, cart, action);
            } catch (SQLException redisplayError) {
                e.addSuppressed(redisplayError);
                throw new ServletException(e);
            }
        }
    }

    private String userMessage(Exception e, String action) {
        if (e instanceof SQLIntegrityConstraintViolationException && "confirmPayment".equals(action)) {
            // UNIQUE (showTimeId, seatId) on tblTicket4: another counter sold the seat first
            return "A selected seat has just been sold at another counter. Please choose seats again.";
        }
        if (e instanceof SQLException) {
            switch (action) {
                case "selectShowTime":
                case "selectRoom":
                case "backToRoom":
                    return "No screening room is available for this show time.";
                case "selectMovie":
                case "backToShowTime":
                case "searchShowTime":
                    return "No show time is available for this movie.";
                case "selectSeat":
                case "deleteSeat":
                case "continueSeat":
                case "backToSeat":
                    return "The seat information is currently unavailable. Please try again.";
                case "checkCard":
                    return "The member card could not be checked. Please try again.";
                case "confirmSell":
                    return "The sale could not be confirmed. Please try again.";
                case "confirmPayment":
                    return "The payment could not be completed. Please try again.";
                default:
                    return "The requested information is currently unavailable. Please try again.";
            }
        }
        String message = e.getMessage();
        return message == null || message.isBlank() ? "Invalid data. Please try again." : message;
    }

    /** Redisplays the current wizard step so a recoverable error does not reset the sale. */
    private void redisplayCurrentScreen(HttpServletRequest request, HttpServletResponse response,
                                        HttpSession cart, String action)
            throws ServletException, IOException, SQLException {
        switch (action) {
            case "selectShowTime":
            case "backToShowTime":
            case "searchShowTime":
                showShowTimeList(request, response, cart);
                break;
            case "selectRoom":
            case "backToRoom":
                showRoomList(request, response, cart);
                break;
            case "selectSeat":
            case "deleteSeat":
            case "continueSeat":
            case "backToSeat":
                showSeatMap(request, response, cart);
                break;
            case "checkCard":
                forwardConfirmSell(request, response, cart);
                break;
            case "confirmSell":
                forwardConfirmSell(request, response, cart);
                break;
            case "confirmPayment":
                showSeatMap(request, response, cart);
                break;
            default:
                forwardSearch(request, response, request.getParameter("movieName"));
        }
    }

    // --- screen 2: search movie ---
    private void forwardSearch(HttpServletRequest request, HttpServletResponse response, String movieName)
            throws ServletException, IOException, SQLException {
        if (movieName != null) {
            String keyword = movieName.trim();
            request.setAttribute("movieName", keyword);
            if (keyword.isEmpty()) {
                request.setAttribute("flowError", "Please enter a movie name.");
            } else {
                List<Movie> movies = movieDAO.searchMovie(keyword);
                if (movies.isEmpty()) {
                    request.setAttribute("flowError", "No movie was found with that name.");
                } else {
                    request.setAttribute("movies", movies);
                }
            }
        }
        request.getRequestDispatcher("seller/ViewSearchMovie.jsp").forward(request, response);
    }

    private void selectMovie(HttpServletRequest request, HttpServletResponse response, HttpSession cart)
            throws ServletException, IOException, SQLException {
        Movie movie = movieDAO.findById(Integer.parseInt(request.getParameter("movieId")));
        if (movie == null) {
            throw new IllegalArgumentException("The selected movie no longer exists.");
        }
        cart.setAttribute("movieId", movie.getId());
        cart.setAttribute("movieName", movie.getName());
        clearAfterMovie(cart);
        showShowTimeList(request, response, cart);
    }

    // --- screen 3: select show time ---
    private void showShowTimeList(HttpServletRequest request, HttpServletResponse response, HttpSession cart)
            throws ServletException, IOException, SQLException {
        LocalDate showDate = (LocalDate) cart.getAttribute("showTimeSearchDate");
        List<ShowTime> showTimes = showDate == null
            ? showTimeDAO.getShowTimeList(movieId(cart))
            : showTimeDAO.getShowTimeList(movieId(cart), showDate);
        request.setAttribute("showTimes", showTimes);
        request.setAttribute("showTimeSearchDate", showDate);
        exposeSaleState(request, cart);
        request.getRequestDispatcher("seller/ViewSelectShowTime.jsp").forward(request, response);
    }

    private void searchShowTime(HttpServletRequest request, HttpServletResponse response, HttpSession cart)
            throws ServletException, IOException, SQLException {
        String value = request.getParameter("showDate");
        if (value == null || value.isBlank()) {
            cart.removeAttribute("showTimeSearchDate");
        } else {
            try {
                cart.setAttribute("showTimeSearchDate", LocalDate.parse(value));
            } catch (DateTimeParseException e) {
                request.setAttribute("flowError", "Please enter a valid show date.");
            }
        }
        showShowTimeList(request, response, cart);
    }

    private void selectShowTime(HttpServletRequest request, HttpServletResponse response, HttpSession cart)
            throws ServletException, IOException, SQLException {
        ShowTime showTime = showTimeDAO.findById(Integer.parseInt(request.getParameter("showTimeId")));
        if (showTime == null || showTime.getMovieId() != movieId(cart)) {
            throw new IllegalArgumentException("Invalid show time for the selected movie.");
        }
        cart.setAttribute("showTimeId", showTime.getId());
        clearAfterShowTime(cart);
        showRoomList(request, response, cart);
    }

    // --- screen 4: select screening room ---
    private void showRoomList(HttpServletRequest request, HttpServletResponse response, HttpSession cart)
            throws ServletException, IOException, SQLException {
        ShowTime showTime = showTimeDAO.findById(showTimeId(cart));
        List<ScreeningRoom> rooms = screeningRoomDAO.getRoomListByShowTime(
            movieId(cart), showTimeId(cart));
        request.setAttribute("showTime", showTime);
        request.setAttribute("rooms", rooms);
        exposeSaleState(request, cart);
        request.getRequestDispatcher("seller/ViewSelectScreeningRoom.jsp").forward(request, response);
    }

    private void selectRoom(HttpServletRequest request, HttpServletResponse response, HttpSession cart)
            throws ServletException, IOException, SQLException {
        ScreeningRoom room = screeningRoomDAO.findByShowTimeAndId(
            movieId(cart), showTimeId(cart), Integer.parseInt(request.getParameter("roomId")));
        if (room == null) {
            throw new IllegalArgumentException("Invalid room for the selected show time.");
        }
        cart.setAttribute("roomId", room.getId());
        cart.setAttribute("roomName", room.getRoomName());
        seatIds(cart).clear();
        showSeatMap(request, response, cart);
    }

    // --- screen 5: select seat ---
    private void showSeatMap(HttpServletRequest request, HttpServletResponse response, HttpSession cart)
            throws ServletException, IOException, SQLException {
        request.setAttribute("seatMap", seatDAO.getSeatMap(showTimeId(cart), roomId(cart)));
        request.setAttribute("seatPrice", ticketDAO.calculatePriceForRoom(showTimeId(cart), roomId(cart)));
        exposeSaleState(request, cart);
        request.getRequestDispatcher("seller/ViewSelectSeat.jsp").forward(request, response);
    }

    private void toggleSeat(HttpServletRequest request, HttpServletResponse response, HttpSession cart, boolean add)
            throws ServletException, IOException, SQLException {
        int seatId = Integer.parseInt(request.getParameter("seatId"));
        if (add) {
            if (seatDAO.checkSeatAvailable(showTimeId(cart), roomId(cart), seatId)) {
                seatIds(cart).add(seatId);
            } else {
                request.setAttribute("seatError",
                    "This seat has already been sold. Please choose another seat.");
            }
        } else {
            seatIds(cart).remove(seatId);
        }
        showSeatMap(request, response, cart);
    }

    // --- screen 6: confirm sale ---
    private void forwardConfirmSell(HttpServletRequest request, HttpServletResponse response, HttpSession cart)
            throws ServletException, IOException, SQLException {
        if (seatIds(cart).isEmpty()) {
            showSeatMap(request, response, cart);
            return;
        }
        exposeSaleState(request, cart);
        request.setAttribute("subtotal", computeSubtotal(cart));
        request.setAttribute("seatLabels", selectedSeatLabels(cart));
        request.getRequestDispatcher("seller/ViewConfirmSell.jsp").forward(request, response);
    }

    private void checkCard(HttpServletRequest request, HttpServletResponse response, HttpSession cart)
            throws ServletException, IOException, SQLException {
        String cardCode = request.getParameter("cardCode");
        double subtotal = computeSubtotal(cart);
        if (cardCode != null && !cardCode.trim().isEmpty()) {
            MemberCard card = memberCardDAO.checkMemberCard(cardCode.trim());
            if (card == null) {
                request.setAttribute("cardError", "Invalid card. Discount = 0, skip this step.");
                cart.removeAttribute("memberCard");
                cart.setAttribute("discountAmount", 0.0);
            } else {
                cart.setAttribute("memberCard", card);
                cart.setAttribute("discountAmount", memberCardDAO.calculateDiscount(card, subtotal));
            }
        }
        exposeSaleState(request, cart);
        request.setAttribute("subtotal", subtotal);
        request.setAttribute("seatLabels", selectedSeatLabels(cart));
        request.getRequestDispatcher("seller/ViewConfirmSell.jsp").forward(request, response);
    }

    private double computeSubtotal(HttpSession cart) throws SQLException {
        double subtotal = 0;
        for (Integer seatId : seatIds(cart)) {
            subtotal += ticketDAO.calculatePrice(showTimeId(cart), seatId);
        }
        return subtotal;
    }

    private String selectedSeatLabels(HttpSession cart) throws SQLException {
        StringBuilder sb = new StringBuilder();
        for (Seat s : seatDAO.getSeatMap(showTimeId(cart), roomId(cart))) {
            if (seatIds(cart).contains(s.getId())) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(s.getLabel());
            }
        }
        return sb.toString();
    }

    // --- screens 7 and 8: payment and display result ---
    private void forwardPayment(HttpServletRequest request, HttpServletResponse response, HttpSession cart)
            throws ServletException, IOException, SQLException {
        if (seatIds(cart).isEmpty()) {
            request.setAttribute("seatError", "Please select at least one seat before continuing.");
            showSeatMap(request, response, cart);
            return;
        }
        if (!selectedSeatsStillAvailable(cart)) {
            seatIds(cart).clear();
            request.setAttribute("seatError",
                "A selected seat has just been sold at another counter. Please select seats again.");
            showSeatMap(request, response, cart);
            return;
        }
        exposeSaleState(request, cart);
        request.setAttribute("subtotal", computeSubtotal(cart));
        request.getRequestDispatcher("seller/ViewPayment.jsp").forward(request, response);
    }

    private boolean selectedSeatsStillAvailable(HttpSession cart) throws SQLException {
        for (Integer seatId : seatIds(cart)) {
            if (!seatDAO.checkSeatAvailable(showTimeId(cart), roomId(cart), seatId)) {
                return false;
            }
        }
        return true;
    }

    private void confirmPayment(HttpServletRequest request, HttpServletResponse response,
                                 HttpSession cart, Seller seller) throws ServletException, IOException, SQLException {
        String method = request.getParameter("method");
        double subtotal = computeSubtotal(cart);
        double finalAmount = subtotal - discountAmount(cart);

        if (seatIds(cart).isEmpty()) {
            cart.setAttribute("paymentError", "Please select at least one seat before paying.");
            showSeatMap(request, response, cart);
            return;
        }

        // Simulated POS terminal. A failed counter payment abandons this selection.
        boolean paid = random.nextInt(6) != 0;
        if (!paid) {
            cart.setAttribute("paymentError", "Payment failed. Please select seats again.");
            seatIds(cart).clear();
            showSeatMap(request, response, cart);
            return;
        }
        cart.removeAttribute("paymentError");

        try (Connection conn = invoiceDAO.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Re-check every seat inside the transaction: another counter may
                // have sold one of them while this seller was at the payment screen.
                for (Integer seatId : seatIds(cart)) {
                    if (!seatDAO.checkSeatAvailable(conn, showTimeId(cart), roomId(cart), seatId)) {
                        conn.rollback();
                        cart.setAttribute("paymentError", "A selected seat has just been sold at another counter. "
                            + "Please choose seats again.");
                        seatIds(cart).clear();
                        showSeatMap(request, response, cart);
                        return;
                    }
                }

                Invoice invoice = new Invoice();
                MemberCard card = memberCard(cart);
                invoice.setCustomerId(card != null ? card.getCustomerId() : null);
                invoice.setSellerId(seller.getId());
                invoice.setTotalAmount(subtotal);
                invoice.setDiscountAmount(discountAmount(cart));
                invoice.setFinalAmount(finalAmount);
                int invoiceId = invoiceDAO.createInvoice(conn, invoice);

                for (Integer seatId : seatIds(cart)) {
                    double price = ticketDAO.calculatePrice(showTimeId(cart), seatId);
                    ticketDAO.createTicket(conn, invoiceId, showTimeId(cart), seatId, price);
                }

                CounterPayment payment = new CounterPayment();
                payment.setInvoiceId(invoiceId);
                payment.setPaymentMethod(method);
                payment.setAmount(finalAmount);
                payment.setStatus(CounterPayment.SUCCESS);
                paymentDAO.createPayment(conn, payment);

                if (card != null) {
                    memberCardDAO.updateLoyaltyPoint(conn, card.getId(), finalAmount);
                    customerStatDAO.updateStat(conn, card.getCustomerId(), finalAmount, seatIds(cart).size());
                }
                movieStatDAO.updateStat(conn, movieId(cart), seatIds(cart).size(), finalAmount);

                conn.commit();
                request.setAttribute("invoice", invoiceDAO.getInvoiceById(invoiceId));
                request.setAttribute("tickets", invoiceDAO.getTicketsByInvoice(invoiceId));
                request.setAttribute("paymentMethod", method);
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }

        request.getRequestDispatcher("seller/ViewPrintInvoice.jsp").forward(request, response);
    }

    private void initializeSaleState(HttpSession session) {
        if (session.getAttribute("seatIds") == null) {
            session.setAttribute("seatIds", new LinkedHashSet<Integer>());
        }
        if (session.getAttribute("discountAmount") == null) {
            session.setAttribute("discountAmount", 0.0);
        }
    }

    @SuppressWarnings("unchecked")
    private Set<Integer> seatIds(HttpSession session) {
        return (Set<Integer>) session.getAttribute("seatIds");
    }

    private int movieId(HttpSession session) {
        return (Integer) session.getAttribute("movieId");
    }

    private int showTimeId(HttpSession session) {
        return (Integer) session.getAttribute("showTimeId");
    }

    private int roomId(HttpSession session) {
        return (Integer) session.getAttribute("roomId");
    }

    private MemberCard memberCard(HttpSession session) {
        return (MemberCard) session.getAttribute("memberCard");
    }

    private double discountAmount(HttpSession session) {
        Object value = session.getAttribute("discountAmount");
        return value == null ? 0 : (Double) value;
    }

    private void clearAfterMovie(HttpSession session) {
        session.removeAttribute("showTimeSearchDate");
        session.removeAttribute("showTimeId");
        clearAfterShowTime(session);
    }

    private void clearAfterShowTime(HttpSession session) {
        session.removeAttribute("roomId");
        session.removeAttribute("roomName");
        seatIds(session).clear();
        session.removeAttribute("memberCard");
        session.setAttribute("discountAmount", 0.0);
        session.removeAttribute("paymentError");
    }

    private void resetSelection(HttpSession session) {
        session.removeAttribute("movieId");
        session.removeAttribute("movieName");
        clearAfterMovie(session);
    }

    private void exposeSaleState(HttpServletRequest request, HttpSession session) {
        request.setAttribute("movieName", session.getAttribute("movieName"));
        request.setAttribute("showTimeId", session.getAttribute("showTimeId"));
        request.setAttribute("roomName", session.getAttribute("roomName"));
        request.setAttribute("seatIds", seatIds(session));
        request.setAttribute("memberCard", memberCard(session));
        request.setAttribute("discountAmount", discountAmount(session));
        request.setAttribute("paymentError", session.getAttribute("paymentError"));
    }
}
