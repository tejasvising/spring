import React from "react";
import { Routes, Route } from "react-router-dom";
import Firstpage from "./FirstPage";
import Offering from "./Offering";
import Appcopyy from "./AboutService"; // Consider renaming for clarity
import Processpage from "./ProcessPage";
import Testimonial from "./Testimonial";
import Footer from "./Footer"
import AuthPage from "../LoginSingup/AuthPage"; // Ensure AuthPage.jsx exists and is exported
import SimpleExamSelectionPage from "../ExamSelectionAndPayment/SimpleExamSelectionPage"; // Ensure SimpleExamSelectionPage.jsx exists and is exported
import Dashboard from "../ExamSelectionAndPayment/Dashboard"; // Ensure Dashboard.jsx exists and is exported
const App = () => {
  return (
    <Routes>
      {/* Landing Page Route */}
      <Route path="/" element={
        <>
          <Firstpage />
          <Appcopyy />
          <Offering />
          <Processpage />
          <Testimonial />
          <Footer />
        </>
      } />

      {/* Auth Page Route */}
      <Route path="/auth" element={<AuthPage />} />
      <Route path="/select" element={<SimpleExamSelectionPage />} />
      <Route path="/dashboard" element={<Dashboard />} />
    </Routes>
  );
};

export default App;
