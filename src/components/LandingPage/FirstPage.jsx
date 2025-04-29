import React, { useState, useEffect } from "react";
import { useNavigate } from 'react-router-dom';
//import { getAuth, onAuthStateChanged, signOut } from "firebase/auth";

const Firstpage = () => {
  const navigate = useNavigate();
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [loading, setLoading] = useState(true); // Add a loading state
  //const auth = getAuth();
  useEffect(() => {
    const userid = localStorage.getItem('userid');
    setIsLoggedIn(!!userid);
  }, []);
  // Check authentication state on component mount
  // useEffect(() => {
  //   const unsubscribe = onAuthStateChanged(auth, (user) => {
  //     if (user) {
  //       // User is signed in
  //       setIsLoggedIn(true);
  //     } else {
  //       // User is signed out
  //       setIsLoggedIn(false);
  //     }
  //     setLoading(false); // Authentication check is complete
  //   });

  //   // Cleanup the listener when the component unmounts
  //   return () => unsubscribe();
  // }, [auth]);

  const handleSignOut = async (e) => {
   
    try {
      
      const res=await fetch('http://localhost:8080/api/auth/logout', {
        method: 'POST',
        credentials: "include",
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          "userid": localStorage.getItem('userid')
        }),
      });
      const data= await res.text();
      console.log("logoutted: ",data);
      localStorage.removeItem('userid');
      localStorage.removeItem('token'); 
      localStorage.removeItem('email'); 
      setIsLoggedIn(false);

    }
      catch(e){
        console.log(e.message);
      }
    // signOut(auth)
    //   .then(() => {
    //     // Sign-out successful
    //     setIsLoggedIn(false);
    //   })
    //   .catch((error) => {
    //     // An error happened
    //     console.error("Sign-out error:", error);
    //   });
  };

  // Render a loading spinner or placeholder until authentication check is complete
  // if (loading) {
  //   return <div className="min-h-screen bg-gray-900 flex items-center justify-center">Loading...</div>;
  // }

  return (
    <div className="min-h-screen bg-gray-900 text-white">
      {/* Navigation */}
      <nav className="flex justify-between items-center p-6">
        <div className="text-xl font-semibold">Counselling</div>
        <div className="flex items-center gap-8">
          <a href="#about" className="hover:text-gray-300">About</a>
          <a href="/dashboard" className="hover:text-gray-300">Dashboard</a>
          <a href="#contact" className="hover:text-gray-300">Contact</a>
          <button
            onClick={() => (!!localStorage.getItem('userid') ? handleSignOut() : navigate("/auth"))}
            className="bg-amber-400 text-black px-4 py-2 rounded-lg hover:bg-amber-500"
          >
            {localStorage.getItem('userid')!=null ? "Logout" : "Sign in"}
          </button>
        </div>
      </nav>

      {/* Main Content */}
      <main className="container mx-auto px-6 py-16 flex items-center justify-between">
        <div className="max-w-xl">
          <h1 className="text-6xl font-bold leading-tight mb-6">
            Predict Your College With Our Algorithm
          </h1>
          <p className="text-gray-300 mb-8">
            Easily enter your NEET, JEE, AKTU, and other competitive exam ranks along with your gender, category, and preferences. Instantly our model will generate a PDF listing government colleges you can get, colleges with the highest ROI, NIRF ranking, and more.
          </p>
          <div className="flex gap-4">
            <button
              className="bg-amber-400 text-black px-6 py-3 rounded-lg hover:bg-amber-500 font-medium"
              onClick={() => {
                !!localStorage.getItem('userId') ? navigate("/select") : navigate("/auth");
              }}
            >
              Get Started
            </button>
            <button className="border-2 border-white px-6 py-3 rounded-lg hover:bg-white hover:text-black transition-colors font-medium">
              Learn More
            </button>
          </div>
        </div>

        {/* Illustration (Image) */}
        <div className="relative">
          <img
            src="/images/handandpaper.png"
            alt="Illustration"
            className="w-120 h-180 object-cover rounded-lg"
          />
        </div>
      </main>
    </div>
  );
};

export default Firstpage;