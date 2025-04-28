import React, { useState, useEffect } from 'react';
import { auth, db, storage } from "../../Firebase/Firebase";
import { doc, setDoc, getDoc, addDoc, collection } from "firebase/firestore";
import { ref, uploadBytes, getDownloadURL } from "firebase/storage";
import { useNavigate } from 'react-router-dom';

const SimpleExamSelectionPage = () => {
  const navigate = useNavigate();
  const [examType, setExamType] = useState('');
  const [category, setCategory] = useState('');
  const [subcategory, setSubCategory] = useState('');
  const [state, setState] = useState('');
  const [rank, setRank] = useState('');
  const [gender, setGender] = useState('');
  const [isFormComplete, setIsFormComplete] = useState(false);
  

  // Check if form is complete whenever dependencies change
  useEffect(() => {
    if (examType === 'neet' && category && subcategory && rank) {
      setIsFormComplete(true);
    }
    else if (examType === 'aktu' && category && subcategory && rank && gender) {
      setIsFormComplete(true);
    }
    else if (examType === 'jee' && category && state && rank) {
      setIsFormComplete(true);
    } else {
      setIsFormComplete(false);
    }
  }, [examType, subcategory, category, state, rank, gender]);

  const handleExamChange = (e) => {
    setExamType(e.target.value);
    // Reset other fields when exam changes
    setCategory('');
    setSubCategory('');
    setState('');
    setRank('');
    setGender('');
  };

  useEffect(() => {
  /*  const params = new URLSearchParams(window.location.search);
  const token = params.get('token');
  const email = params.get('email');
  const userid = params.get('userid');
  if (token) {
    localStorage.setItem('token', token);
    localStorage.setItem('email', email);
    localStorage.setItem('userid', userid);
    // Now user is considered "signed in"
  }
  */
    // Load Razorpay script dynamically
    const script = document.createElement('script');
    script.src = 'https://checkout.razorpay.com/v1/checkout.js';
    script.async = true;
    document.body.appendChild(script);
    
    return () => {
      document.body.removeChild(script);
    }
  }, []);

  // const user = auth.currentUser;

  const pdfgen_details = {
    "userID": localStorage.getItem('userid'),  
    "AIR": rank,
    "Category": category,
    ...(examType === "neet" && { "SubCategory": subcategory }),
    ...(examType === "aktu" && { "SubCategory": subcategory, "Gender": gender }),
    ...(examType === "jee" ? { "State": state } : {}),
    "ExamType": examType
  };


  // fetching from env file
  const apiBaseUrl = process.env.REACT_APP_API_BASE_URL;

  const handlePayment = async () => {
    try {
      // Fetch Razorpay key
      const keyResponse = await fetch(`${apiBaseUrl}/get-key`,{
        method:'GET',
        credentials: "include",
        headers: {
          
          'Content-Type': 'application/json'
        },
        
      });
      const { key_id } = await keyResponse.json();

      // Create order
        const orderResponse = await fetch(`${apiBaseUrl}/order`, {
        method: 'POST',
        credentials: "include",
        headers: {
      //    'Authorization': `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/json'
        },
      });
      const order = await orderResponse.json();
      console.log("order: ",order);
      // Razorpay option
      const options = {
        key: key_id,
        amount: order.amount,
        currency: "INR",
        name: "AcmeCorp",
        description: "Test Transaction",
        order_id: order.id,
        handler: async function(response) {
          // Verify payment through backend
          const formData = new FormData();
          formData.append('razorpay_payment_id', response.razorpay_payment_id);
          formData.append('razorpay_order_id', response.razorpay_order_id);
          formData.append('razorpay_signature', response.razorpay_signature);

          // here we will pass form Data to backend via verify api post request
          formData.append('details', JSON.stringify(pdfgen_details));
          const payload = {
            razorpay_payment_id: response.razorpay_payment_id,
            razorpay_order_id: response.razorpay_order_id,
            razorpay_signature: response.razorpay_signature,
            details: pdfgen_details
          };
          
          for (let pair of formData.entries()) {
            console.log(`${pair[0]}: ${pair[1]}`);
          }
          await fetch('http://127.0.0.1:8080/verify', {
            method: 'POST',
            
            headers: {
              'Authorization': `Bearer ${localStorage.getItem('token')}`,
              'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload),
          });
          navigate("/dashboard");
        },
        theme: {
          color: "#6366f1"
        }
      };

      const rzp = new window.Razorpay(options);
    //  navigate("/");
      rzp.open();
     // navigate("/dashboard");
    } catch (error) {
      console.error('Payment failed:', error);
      alert('Payment failed! Please try again.');
    }
  };

  return (
    <div style={{
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      minHeight: '100vh',
      background: 'linear-gradient(135deg, #f6f9fc 0%, #e9f0f7 100%)',
      padding: '20px',
      fontFamily: "'Poppins', 'Segoe UI', Roboto, sans-serif"
    }}>
      <div style={{
        width: '100%',
        maxWidth: '550px',
        backgroundColor: 'white',
        borderRadius: '16px',
        boxShadow: '0 10px 30px rgba(0,0,0,0.07)',
        padding: '40px',
        overflow: 'hidden',
        position: 'relative'
      }}>
        <div style={{
          position: 'absolute',
          top: 0,
          left: 0,
          width: '100%',
          height: '6px',
          background: 'linear-gradient(90deg, #6366f1, #818cf8)'
        }}></div>

        <h1 style={{ 
          fontSize: '28px', 
          marginBottom: '8px', 
          fontWeight: '700', 
          color: '#1f2937', 
          letterSpacing: '-0.025em'
        }}>Exam Registration</h1>
        <p style={{ 
          color: '#6b7280', 
          marginBottom: '32px', 
          fontSize: '16px' 
        }}>Select your exam and enter your details below</p>
        
        <div style={{ marginBottom: '24px' }}>
          <label htmlFor="exam-select" style={{ 
            display: 'block', 
            marginBottom: '8px', 
            fontWeight: '500',
            color: '#4b5563',
            fontSize: '15px'
          }}>
            Select Exam
          </label>
          <select 
            id="exam-select"
            value={examType}
            onChange={handleExamChange}
            style={{
              width: '100%',
              padding: '12px 16px',
              borderRadius: '8px',
              border: '1px solid #e5e7eb',
              backgroundColor: '#f9fafb',
              color: '#1f2937',
              fontSize: '15px',
              appearance: 'none',
              backgroundImage: 'url("data:image/svg+xml;charset=US-ASCII,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22292.4%22%20height%3D%22292.4%22%3E%3Cpath%20fill%3D%22%23555555%22%20d%3D%22M287%2069.4a17.6%2017.6%200%200%200-13-5.4H18.4c-5%200-9.3%201.8-12.9%205.4A17.6%2017.6%200%200%200%200%2082.2c0%205%201.8%209.3%205.4%2012.9l128%20127.9c3.6%203.6%207.8%205.4%2012.8%205.4s9.2-1.8%2012.8-5.4L287%2095c3.5-3.5%205.4-7.8%205.4-12.8%200-5-1.9-9.2-5.5-12.8z%22%2F%3E%3C%2Fsvg%3E")',
              backgroundRepeat: 'no-repeat',
              backgroundPosition: 'right 16px center',
              backgroundSize: '12px',
              boxShadow: '0 1px 2px rgba(0, 0, 0, 0.05)',
              transition: 'all 0.2s ease',
              outline: 'none'
            }}
          >
            <option value="" disabled>Select exam type</option>
            <option value="aktu">AKTU</option>
            <option value="jee">JEE</option>
            <option value="neet">NEET</option>
          </select>
        </div>

        {examType && (
          <div style={{ 
            animation: 'fadeIn 0.5s ease-out',
            display: 'flex',
            flexDirection: 'column',
            gap: '24px'
          }}>
            {/* Gender field - only for AKTU */}
            {examType === 'aktu' && (
              <div>
                <label htmlFor="gender-select" style={{ 
                  display: 'block', 
                  marginBottom: '8px', 
                  fontWeight: '500',
                  color: '#4b5563',
                  fontSize: '15px'
                }}>
                  Select Gender
                </label>
                <select 
                  id="gender-select"
                  value={gender}
                  onChange={(e) => setGender(e.target.value)}
                  style={{
                    width: '100%',
                    padding: '12px 16px',
                    borderRadius: '8px',
                    border: '1px solid #e5e7eb',
                    backgroundColor: '#f9fafb',
                    color: '#1f2937',
                    fontSize: '15px',
                    appearance: 'none',
                    backgroundImage: 'url("data:image/svg+xml;charset=US-ASCII,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22292.4%22%20height%3D%22292.4%22%3E%3Cpath%20fill%3D%22%23555555%22%20d%3D%22M287%2069.4a17.6%2017.6%200%200%200-13-5.4H18.4c-5%200-9.3%201.8-12.9%205.4A17.6%2017.6%200%200%200%200%2082.2c0%205%201.8%209.3%205.4%2012.9l128%20127.9c3.6%203.6%207.8%205.4%2012.8%205.4s9.2-1.8%2012.8-5.4L287%2095c3.5-3.5%205.4-7.8%205.4-12.8%200-5-1.9-9.2-5.5-12.8z%22%2F%3E%3C%2Fsvg%3E")',
                    backgroundRepeat: 'no-repeat',
                    backgroundPosition: 'right 16px center',
                    backgroundSize: '12px',
                    boxShadow: '0 1px 2px rgba(0, 0, 0, 0.05)',
                    transition: 'all 0.2s ease',
                    outline: 'none'
                  }}
                >
                  <option value="" disabled>Select your gender</option>
                  <option value="Male">Male</option>
                  <option value="Female">Female</option>
                </select>
              </div>
            )}

            <div>
              <label htmlFor="category-select" style={{ 
                display: 'block', 
                marginBottom: '8px', 
                fontWeight: '500',
                color: '#4b5563',
                fontSize: '15px'
              }}>
                Select Category
              </label>
              <select 
                id="category-select"
                value={category}
                onChange={(e) => setCategory(e.target.value)}
                style={{
                  width: '100%',
                  padding: '12px 16px',
                  borderRadius: '8px',
                  border: '1px solid #e5e7eb',
                  backgroundColor: '#f9fafb',
                  color: '#1f2937',
                  fontSize: '15px',
                  appearance: 'none',
                  backgroundImage: 'url("data:image/svg+xml;charset=US-ASCII,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22292.4%22%20height%3D%22292.4%22%3E%3Cpath%20fill%3D%22%23555555%22%20d%3D%22M287%2069.4a17.6%2017.6%200%200%200-13-5.4H18.4c-5%200-9.3%201.8-12.9%205.4A17.6%2017.6%200%200%200%200%2082.2c0%205%201.8%209.3%205.4%2012.9l128%20127.9c3.6%203.6%207.8%205.4%2012.8%205.4s9.2-1.8%2012.8-5.4L287%2095c3.5-3.5%205.4-7.8%205.4-12.8%200-5-1.9-9.2-5.5-12.8z%22%2F%3E%3C%2Fsvg%3E")',
                  backgroundRepeat: 'no-repeat',
                  backgroundPosition: 'right 16px center',
                  backgroundSize: '12px',
                  boxShadow: '0 1px 2px rgba(0, 0, 0, 0.05)',
                  transition: 'all 0.2s ease',
                  outline: 'none'
                }}
              >
                <option value="" disabled>Select your category</option>
                {examType === 'aktu' ? (
                  <>
                    <option value="OPEN">OPEN</option>
                    <option value="EWS">EWS</option>
                    <option value="SC">SC</option>
                    <option value="ST">ST</option>
                  </>
                ) : (
                  <>
                    <option value="general">General</option>
                    <option value="obc">OBC</option>
                    <option value="sc">SC</option>
                    <option value="st">ST</option>
                  </>
                )}
              </select>
            </div>
            
            {/* SubCategory field - for both NEET and AKTU now */}
            {(examType === 'neet' || examType === 'aktu') && (
              <div>
                <label htmlFor="subcategory-select" style={{ 
                  display: 'block', 
                  marginBottom: '8px', 
                  fontWeight: '500',
                  color: '#4b5563',
                  fontSize: '15px'
                }}>
                  Select SubCategory
                </label>
                <select 
                  id="subcategory-select"
                  value={subcategory}
                  onChange={(e) => setSubCategory(e.target.value)}
                  style={{
                    width: '100%',
                    padding: '12px 16px',
                    borderRadius: '8px',
                    border: '1px solid #e5e7eb',
                    backgroundColor: '#f9fafb',
                    color: '#1f2937',
                    fontSize: '15px',
                    appearance: 'none',
                    backgroundImage: 'url("data:image/svg+xml;charset=US-ASCII,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22292.4%22%20height%3D%22292.4%22%3E%3Cpath%20fill%3D%22%23555555%22%20d%3D%22M287%2069.4a17.6%2017.6%200%200%200-13-5.4H18.4c-5%200-9.3%201.8-12.9%205.4A17.6%2017.6%200%200%200%200%2082.2c0%205%201.8%209.3%205.4%2012.9l128%20127.9c3.6%203.6%207.8%205.4%2012.8%205.4s9.2-1.8%2012.8-5.4L287%2095c3.5-3.5%205.4-7.8%205.4-12.8%200-5-1.9-9.2-5.5-12.8z%22%2F%3E%3C%2Fsvg%3E")',
                    backgroundRepeat: 'no-repeat',
                    backgroundPosition: 'right 16px center',
                    backgroundSize: '12px',
                    boxShadow: '0 1px 2px rgba(0, 0, 0, 0.05)',
                    transition: 'all 0.2s ease',
                    outline: 'none'
                  }}
                > 
                  <option value="" disabled>Select your subcategory</option>
                  {examType === 'aktu' ? (
                    <>
                      <option value="FF">FF</option>
                      <option value="AF">AF</option>
                      <option value="PH">PH</option>
                      <option value="GL">GL</option>
                      <option value="OPEN">OPEN</option>
                    </>
                  ) : (
                    <>
                      <option value="No">No</option>
                      <option value="PwD">PwD</option>
                    </>
                  )}
                </select>
              </div>
            )}

            {/* State field - conditional rendering */}
            {(examType === 'jee' || examType === 'neet') && (
              <div>
                <label htmlFor="state-select" style={{ 
                  display: 'block', 
                  marginBottom: '8px', 
                  fontWeight: '500',
                  color: '#4b5563',
                  fontSize: '15px'
                }}>
                  Select State
                </label>
                <select 
                  id="state-select"
                  value={state}
                  onChange={(e) => setState(e.target.value)}
                  style={{
                    width: '100%',
                    padding: '12px 16px',
                    borderRadius: '8px',
                    border: '1px solid #e5e7eb',
                    backgroundColor: '#f9fafb',
                    color: '#1f2937',
                    fontSize: '15px',
                    appearance: 'none',
                    backgroundImage: 'url("data:image/svg+xml;charset=US-ASCII,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22292.4%22%20height%3D%22292.4%22%3E%3Cpath%20fill%3D%22%23555555%22%20d%3D%22M287%2069.4a17.6%2017.6%200%200%200-13-5.4H18.4c-5%200-9.3%201.8-12.9%205.4A17.6%2017.6%200%200%200%200%2082.2c0%205%201.8%209.3%205.4%2012.9l128%20127.9c3.6%203.6%207.8%205.4%2012.8%205.4s9.2-1.8%2012.8-5.4L287%2095c3.5-3.5%205.4-7.8%205.4-12.8%200-5-1.9-9.2-5.5-12.8z%22%2F%3E%3C%2Fsvg%3E")',
                    backgroundRepeat: 'no-repeat',
                    backgroundPosition: 'right 16px center',
                    backgroundSize: '12px',
                    boxShadow: '0 1px 2px rgba(0, 0, 0, 0.05)',
                    transition: 'all 0.2s ease',
                    outline: 'none'
                  }}
                >
                  <option value="" disabled>Select your state</option>
                  {examType === 'jee' ? (
                    <>
                      <option value="uttarpradesh">Uttar Pradesh</option>
                      <option value="delhi">Delhi</option>
                      <option value="haryana">Haryana</option>
                      <option value="otherstate">Other State</option>
                    </>
                  ) : (
                    <>
                      <option value="uttarpradesh">Uttar Pradesh</option>
                      <option value="kerala">Kerala</option>
                      <option value="tamilnadu">Tamil Nadu</option>
                      <option value="otherstate">Other State</option>
                    </>
                  )}
                </select>
              </div>
            )}

            <div>
              <label htmlFor="rank-input" style={{ 
                display: 'block', 
                marginBottom: '8px', 
                fontWeight: '500',
                color: '#4b5563',
                fontSize: '15px'
              }}>
                Enter Your Rank
              </label>
              <input
                id="rank-input"
                type="number"
                placeholder="Enter your rank"
                value={rank}
                onChange={(e) => setRank(e.target.value)}
                style={{
                  width: '100%',
                  padding: '12px 16px',
                  borderRadius: '8px',
                  border: '1px solid #e5e7eb',
                  backgroundColor: '#f9fafb',
                  color: '#1f2937',
                  fontSize: '15px',
                  boxShadow: '0 1px 2px rgba(0, 0, 0, 0.05)',
                  transition: 'all 0.2s ease',
                  outline: 'none'
                }}
              />
            </div>
          </div>
        )}

        <div style={{ marginTop: '32px' }}>
          <button 
            disabled={!isFormComplete}
            onClick={handlePayment}
            style={{
              width: '100%',
              padding: '14px',
              backgroundColor: isFormComplete ? '#6366f1' : '#e5e7eb',
              color: isFormComplete ? 'white' : '#9ca3af',
              border: 'none',
              borderRadius: '8px',
              cursor: isFormComplete ? 'pointer' : 'not-allowed',
              fontSize: '16px',
              fontWeight: '500',
              transition: 'all 0.2s ease',
              boxShadow: isFormComplete ? '0 4px 6px rgba(99, 102, 241, 0.25)' : 'none',
              position: 'relative',
              overflow: 'hidden'
            }}
          >
            {isFormComplete ? (
              <>
                <span>Continue to Payment</span>
                <span style={{
                  marginLeft: '8px',
                  display: 'inline-block',
                }}>→</span>
              </>
            ) : 'Please Complete Form'}
          </button>
        </div>
        
        <div style={{
          textAlign: 'center',
          marginTop: '20px',
          fontSize: '14px',
          color: '#6b7280'
        }}>
          By proceeding, you agree to our Terms of Service
        </div>

        <style>{`
          @keyframes fadeIn {
            from { opacity: 0; transform: translateY(10px); }
            to { opacity: 1; transform: translateY(0); }
          }
          
          select:focus, input:focus {
            border-color: #818cf8 !important;
            box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.15) !important;
          }
          
          button:hover:not(:disabled) {
            background-color: #4f46e5 !important;
            transform: translateY(-1px);
          }
          
          button:active:not(:disabled) {
            transform: translateY(0);
          }
        `}</style>
      </div>
    </div>
  );
};

export default SimpleExamSelectionPage;